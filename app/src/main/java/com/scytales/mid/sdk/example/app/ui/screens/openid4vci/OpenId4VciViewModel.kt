package com.scytales.mid.sdk.example.app.ui.screens.openid4vci

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.example.app.openid4vci.OpenId4VciCoordinator
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import eu.europa.ec.eudi.wallet.document.DocumentExtensions.getDefaultCreateDocumentSettings
import eu.europa.ec.eudi.wallet.issue.openid4vci.IssueEvent
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import eu.europa.ec.eudi.wallet.issue.openid4vci.OfferResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for OpenID4VCI document issuance.
 *
 * Collects from Coordinator's flows and updates UI state.
 * The shared flows survive ViewModel recreation during authorization.
 */
class OpenId4VciViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "OpenId4VciViewModel"
        private const val KEY_OFFER_URI = "openid4vci_offer_uri"
    }

    private val _offerState = MutableStateFlow<OfferState>(OfferState.Idle)
    val offerState: StateFlow<OfferState> = _offerState.asStateFlow()

    private val _issuanceProgress = MutableStateFlow<IssuanceProgress>(IssuanceProgress.Idle)
    val issuanceProgress: StateFlow<IssuanceProgress> = _issuanceProgress.asStateFlow()

    fun saveOfferUri(offerUri: String) {
        savedStateHandle[KEY_OFFER_URI] = offerUri
    }

    fun getSavedOfferUri(): String? =
        savedStateHandle.get<String>(KEY_OFFER_URI)

    fun resolveOffer(offerUri: String) {
        viewModelScope.launch {
            if (!ScytalesSdkInitializer.isInitialized()) {
                _offerState.value = OfferState.Error("SDK not initialized", IllegalStateException())
                return@launch
            }

            _offerState.value = OfferState.Resolving
            saveOfferUri(offerUri)

            OpenId4VciCoordinator.resolveOffer(offerUri)
                .catch { e ->
                    _offerState.value = OfferState.Error("Failed to resolve offer", e)
                }
                .collect { result ->
                    _offerState.value = when (result) {
                        is OfferResult.Success -> OfferState.Resolved(result.offer)
                        is OfferResult.Failure -> OfferState.Error(
                            "Failed to resolve offer",
                            result.cause
                        )
                    }
                }
        }
    }

    fun issueDocuments(offer: Offer, txCode: String?) {
        viewModelScope.launch {
            if (!ScytalesSdkInitializer.isInitialized()) {
                _issuanceProgress.value =
                    IssuanceProgress.Error("SDK not initialized", IllegalStateException())
                return@launch
            }

            val issuedDocumentIds = mutableListOf<String>()
            OpenId4VciCoordinator.issueDocuments(offer, txCode)
                .catch { e ->
                    _issuanceProgress.value = IssuanceProgress.Error("Issuance failed", e)
                }
                .collect { event -> handleIssueEvent(event, issuedDocumentIds) }
        }
    }

    fun resumeAuthorization(uri: Uri) {
        viewModelScope.launch {
            if (!OpenId4VciCoordinator.resumeWithAuthorization(uri)) {
                _issuanceProgress.value = IssuanceProgress.Error(
                    "Authorization failed",
                    IllegalStateException("No active session")
                )
                return@launch
            }

            val activeFlow = OpenId4VciCoordinator.getActiveIssueFlow()
            if (activeFlow == null) {
                _issuanceProgress.value = IssuanceProgress.Error(
                    "Issuance state lost",
                    IllegalStateException("No active flow")
                )
                return@launch
            }

            val issuedDocumentIds = mutableListOf<String>()
            activeFlow
                .catch { e ->
                    _issuanceProgress.value = IssuanceProgress.Error("Issuance failed", e)
                }
                .collect { event -> handleIssueEvent(event, issuedDocumentIds) }
        }
    }

    private fun handleIssueEvent(event: IssueEvent, issuedDocumentIds: MutableList<String>) {
        when (event) {
            is IssueEvent.Started -> {
                _issuanceProgress.value = IssuanceProgress.Issuing(event.total, 0)
            }

            is IssueEvent.DocumentRequiresCreateSettings -> {
                val settings = ScytalesSdkInitializer.getSdk().getDefaultCreateDocumentSettings(
                    offeredDocument = event.offeredDocument,
                    numberOfCredentials = 1,
                    credentialPolicy = eu.europa.ec.eudi.wallet.document.CreateDocumentSettings.CredentialPolicy.RotateUse
                )
                event.resume(settings)
            }

            is IssueEvent.DocumentIssued -> {
                issuedDocumentIds.add(event.documentId)
                (_issuanceProgress.value as? IssuanceProgress.Issuing)?.let {
                    _issuanceProgress.value = it.copy(issued = issuedDocumentIds.size)
                }
            }

            is IssueEvent.Finished -> {
                _issuanceProgress.value = IssuanceProgress.Success(issuedDocumentIds)
            }

            is IssueEvent.Failure -> {
                _issuanceProgress.value = IssuanceProgress.Error("Issuance failed", event.cause)
            }

            is IssueEvent.DocumentRequiresUserAuth -> {
                event.resume(event.keysRequireAuth.mapValues { null })
            }

            is IssueEvent.DocumentFailed -> {
                Log.e(TAG, event.documentId, event.cause)
            }

            is IssueEvent.DocumentDeferred -> {
                Log.d(TAG, event.toString())
            }
        }
    }

    fun resetState() {
        _offerState.value = OfferState.Idle
        _issuanceProgress.value = IssuanceProgress.Idle
        savedStateHandle.remove<String>(KEY_OFFER_URI)
    }
}

/**
 * Offer resolution state
 */
sealed class OfferState {
    data object Idle : OfferState()
    data object Resolving : OfferState()
    data class Resolved(val offer: Offer) : OfferState()
    data class Error(val message: String, val error: Throwable) : OfferState()
}

/**
 * Detailed issuance progress for UI display
 */
sealed class IssuanceProgress {
    data object Idle : IssuanceProgress()
    data class Issuing(val total: Int, val issued: Int) : IssuanceProgress()
    data class Success(val documentIds: List<String>) : IssuanceProgress()
    data class Error(val message: String, val error: Throwable) : IssuanceProgress()
}

