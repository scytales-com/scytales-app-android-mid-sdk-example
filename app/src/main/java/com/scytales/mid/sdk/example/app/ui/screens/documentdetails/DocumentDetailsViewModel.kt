package com.scytales.mid.sdk.example.app.ui.screens.documentdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.Sdk
import com.scytales.mid.sdk.example.app.sdk.DocumentOrigin
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import com.scytales.mid.sdk.example.app.sdk.originOf
import com.scytales.mid.sdk.manager.reissue.ReIssueException
import eu.europa.ec.eudi.wallet.document.CreateDocumentSettings
import eu.europa.ec.eudi.wallet.document.DocumentExtensions.getDefaultCreateDocumentSettings
import eu.europa.ec.eudi.wallet.document.DocumentExtensions.getDefaultCreateKeySettings
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.issue.openid4vci.IssueEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * ViewModel for document details screen
 */
class DocumentDetailsViewModel(
    initialDocumentId: String
) : ViewModel() {

    // A re-issue stores the new batch as a new document, which this screen then shows
    private var documentId: String = initialDocumentId

    private val _state = MutableStateFlow<DocumentDetailsState>(DocumentDetailsState.Loading)
    val state: StateFlow<DocumentDetailsState> = _state.asStateFlow()

    private val _isReissuing = MutableStateFlow(false)
    val isReissuing: StateFlow<Boolean> = _isReissuing.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)

    /**
     * Result of the last re-issue, shown once by the screen
     */
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        loadDocumentDetails()
    }

    fun loadDocumentDetails() {
        viewModelScope.launch {
            _state.value = DocumentDetailsState.Loading

            try {
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = DocumentDetailsState.Error("SDK not initialized")
                    return@launch
                }

                val sdk = ScytalesSdkInitializer.getSdk()
                val document = sdk.getDocumentById(documentId) as? IssuedDocument

                if (document == null) {
                    _state.value = DocumentDetailsState.Error("Document not found")
                    return@launch
                }

                val details = document.toDocumentDetails()
                _state.value = DocumentDetailsState.Success(details)

            } catch (e: Exception) {
                Log.e(TAG, "Error loading document details", e)
                _state.value = DocumentDetailsState.Error("Failed to load document: ${e.message}")
            }
        }
    }

    fun deleteDocument() {
        viewModelScope.launch {
            try {
                if (!ScytalesSdkInitializer.isInitialized()) {
                    return@launch
                }

                val sdk = ScytalesSdkInitializer.getSdk()
                sdk.deleteDocumentById(documentId)
                Log.d(TAG, "Document deleted: $documentId")

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting document", e)
            }
        }
    }

    /**
     * Re-issue the document through the flow that issued it: the issuer sends a new batch of
     * credentials, stored as a new document that replaces the old one.
     */
    fun reissueDocument() {
        viewModelScope.launch {
            val sdk = ScytalesSdkInitializer.getSdk()
            val document = sdk.getDocumentById(documentId) as? IssuedDocument ?: return@launch

            _isReissuing.value = true
            val result = when (sdk.originOf(document)) {
                DocumentOrigin.ScytalesSignup -> reissueWithScytalesManager(sdk, document)
                DocumentOrigin.OpenId4Vci -> reissueWithOpenId4Vci(sdk, document)
            }
            result.fold(
                onSuccess = { reissuedId ->
                    Log.d(TAG, "Re-issued ${document.id} as $reissuedId")
                    documentId = reissuedId
                    val count = (sdk.getDocumentById(reissuedId) as? IssuedDocument)?.credentialsCount()
                    _message.value = "Re-issued: $count new credentials"
                    loadDocumentDetails()
                },
                onFailure = { error ->
                    Log.e(TAG, "Re-issue failed", error)
                    val reason = (error as? ReIssueException)?.code ?: error.message
                    _message.value = "Re-issue failed ($reason)"
                }
            )
            _isReissuing.value = false
        }
    }

    /**
     * Signup: the re-issue manager stores the new batch as a new document, and the old one is
     * deleted here.
     */
    private suspend fun reissueWithScytalesManager(sdk: Sdk, document: IssuedDocument): Result<String> =
        sdk.createReIssueManager().reissueDocument(document, null).map { reissued ->
            sdk.deleteDocumentById(document.id)
            reissued.id
        }

    /**
     * OpenID4VCI: Wallet Core re-issues with the tokens stored at issuance, without a browser
     * login, and deletes the old document itself. Expired tokens fail with
     * ReissuanceAuthorizationException.
     */
    private suspend fun reissueWithOpenId4Vci(sdk: Sdk, document: IssuedDocument): Result<String> =
        suspendCancellableCoroutine { continuation ->
            fun finish(result: Result<String>) {
                if (continuation.isActive) continuation.resume(result)
            }
            sdk.createOpenId4VciManager().reissueDocument(
                documentId = document.id,
                allowAuthorizationFallback = false,
                executor = null
            ) { event ->
                when (event) {
                    is IssueEvent.DocumentRequiresCreateSettings.OptionalReusePolicy -> event.resume(
                        sdk.getDefaultCreateDocumentSettings(
                            offeredDocument = event.offeredDocument,
                            credentialPolicy = CreateDocumentSettings.CredentialPolicy
                                .RotatingBatch(numberOfCredentials = 1)
                        )
                    )

                    is IssueEvent.DocumentRequiresCreateSettings.MandatoryReusePolicy -> {
                        val (secureAreaIdentifier, createKeySettings) = sdk.getDefaultCreateKeySettings()
                        event.resume(secureAreaIdentifier, createKeySettings)
                    }

                    is IssueEvent.DocumentRequiresUserAuth ->
                        event.resume(event.keysRequireAuth.mapValues { null })

                    is IssueEvent.Finished -> finish(
                        event.issuedDocuments.firstOrNull()?.let { Result.success(it) }
                            ?: Result.failure(IllegalStateException("The issuer has not issued the document yet"))
                    )

                    is IssueEvent.Failure -> finish(Result.failure(event.cause))

                    else -> Unit
                }
            }
        }

    /**
     * Clear the message once the screen has shown it
     */
    fun onMessageShown() {
        _message.value = null
    }

    companion object {
        private const val TAG = "DocumentDetailsViewModel"
    }
}

/**
 * Factory for creating DocumentDetailsViewModel with documentId parameter
 */
class DocumentDetailsViewModelFactory(
    private val documentId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocumentDetailsViewModel::class.java)) {
            return DocumentDetailsViewModel(documentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

