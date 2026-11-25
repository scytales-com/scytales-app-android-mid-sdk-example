package com.scytales.mid.sdk.example.app.ui.screens.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.Sdk
import com.scytales.mid.sdk.example.app.sdk.DocumentOrigin
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import com.scytales.mid.sdk.example.app.sdk.originOf
import com.scytales.mid.sdk.example.app.ui.screens.home.toDocumentItem
import com.scytales.mid.sdk.example.app.ui.screens.home.toPendingDocumentItem
import com.scytales.mid.sdk.manager.signup.DeferredIssuanceException
import com.scytales.mid.sdk.manager.signup.DeferredIssuanceOutcome
import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.issue.openid4vci.DeferredIssueResult
import eu.europa.ec.eudi.wallet.issue.openid4vci.OpenId4VciManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * ViewModel for the document list screen
 *
 * Manages loading and displaying issued documents, and pending (deferred) documents the
 * issuer has not decided yet, from the SDK
 */
class DocumentListViewModel : ViewModel() {

    private val _state = MutableStateFlow<DocumentListState>(DocumentListState.Loading)
    val state: StateFlow<DocumentListState> = _state.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)

    /**
     * Result of the last pending check, shown once by the screen
     */
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        loadDocuments()
    }

    /**
     * Load issued and pending documents from the SDK, pending ones first
     * Can be called externally to refresh the document list
     */
    fun loadDocuments() {
        viewModelScope.launch {
            _state.value = DocumentListState.Loading

            try {
                // Check if SDK is initialized
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = DocumentListState.Error("SDK not initialized. Please restart the app.")
                    return@launch
                }

                // Get SDK instance
                val sdk = ScytalesSdkInitializer.getSdk()

                // Issued documents, and deferred documents waiting for the issuer
                val documents = sdk.getDocuments { document ->
                    document is IssuedDocument || document is DeferredDocument
                }

                Log.d(TAG, "Loaded ${documents.size} documents")

                // Map to UI items
                val documentItems = documents
                    .mapNotNull { document ->
                        when (document) {
                            is DeferredDocument -> document.toPendingDocumentItem()
                            is IssuedDocument -> document.toDocumentItem()
                            else -> null
                        }
                    }
                    .sortedByDescending { it.isPending }

                // Update state
                _state.value = if (documentItems.isEmpty()) {
                    DocumentListState.Empty
                } else {
                    DocumentListState.Success(documentItems)
                }

            } catch (e: IllegalStateException) {
                Log.e(TAG, "SDK not initialized", e)
                _state.value = DocumentListState.Error("SDK not initialized. Please restart the app.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading documents", e)
                _state.value = DocumentListState.Error("Failed to load documents: ${e.message}")
            }
        }
    }

    /**
     * A tap on a document: a pending one is checked with the issuer, an issued one opens
     */
    fun onDocumentClick(documentId: String, openDetails: (String) -> Unit) {
        val document = (_state.value as? DocumentListState.Success)?.documents
            ?.firstOrNull { it.id == documentId }
        if (document?.isPending == true) {
            checkPending(documentId)
        } else {
            openDetails(documentId)
        }
    }

    /**
     * Ask the issuer once whether a pending document is ready, then reload the list
     */
    private fun checkPending(documentId: String) {
        viewModelScope.launch {
            val sdk = ScytalesSdkInitializer.getSdk()
            val document = sdk.getDocumentById(documentId) as? DeferredDocument
            if (document != null) {
                // Collect through the flow that deferred the document
                _message.value = when (sdk.originOf(document)) {
                    DocumentOrigin.ScytalesSignup -> collectSignupDocument(sdk, document)
                    DocumentOrigin.OpenId4Vci -> collectOpenId4VciDocument(sdk, document)
                }
            }
            loadDocuments()
        }
    }

    /**
     * Clear the message once the screen has shown it
     */
    fun onMessageShown() {
        _message.value = null
    }

    private suspend fun collectSignupDocument(sdk: Sdk, document: DeferredDocument): String =
        sdk.createSignManager().collectDeferredDocument(document).fold(
            onSuccess = { outcome ->
                Log.d(TAG, "Collected ${document.id}: $outcome")
                when (outcome) {
                    is DeferredIssuanceOutcome.Issued ->
                        "${outcome.document.name} is ready"

                    is DeferredIssuanceOutcome.Pending ->
                        "Still pending. The issuer asks to wait ${outcome.interval.inWholeSeconds} s"

                    is DeferredIssuanceOutcome.Rejected ->
                        "Not issued: ${outcome.reasons.ifEmpty { listOfNotNull(outcome.code) }.joinToString()}"
                }
            },
            onFailure = { error ->
                // The document is kept, so the user can check again later
                Log.e(TAG, "Collecting ${document.id} failed", error)
                val reason = (error as? DeferredIssuanceException)?.code ?: error.message
                "Could not check ($reason). The document is kept."
            }
        )

    private suspend fun collectOpenId4VciDocument(sdk: Sdk, document: DeferredDocument): String =
        suspendCancellableCoroutine { continuation ->
            sdk.createOpenId4VciManager().issueDeferredDocument(
                document,
                null,
                OpenId4VciManager.OnDeferredIssueResult { result ->
                    Log.d(TAG, "Collected ${document.id}: $result")
                    val message = when (result) {
                        is DeferredIssueResult.DocumentIssued -> "${result.name} is ready"
                        is DeferredIssueResult.DocumentNotReady -> "Still pending"
                        is DeferredIssueResult.DocumentExpired -> "Not issued: the issuance expired"
                        is DeferredIssueResult.DocumentFailed ->
                            "Could not check (${result.cause.message}). The document is kept."
                    }
                    if (continuation.isActive) continuation.resume(message)
                }
            )
        }

    companion object {
        private const val TAG = "DocumentListViewModel"
    }
}
