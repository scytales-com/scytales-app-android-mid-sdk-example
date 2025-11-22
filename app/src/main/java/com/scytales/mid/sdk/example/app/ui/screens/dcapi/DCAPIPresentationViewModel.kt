package com.scytales.mid.sdk.example.app.ui.screens.dcapi

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import eu.europa.ec.eudi.iso18013.transfer.TransferEvent
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocument
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocuments
import eu.europa.ec.eudi.iso18013.transfer.response.RequestProcessor
import eu.europa.ec.eudi.iso18013.transfer.response.RequestedDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing DCAPI (Digital Credentials API) presentation
 *
 * DCAPI is browser-initiated credential verification that follows the ISO/IEC TS 18013-7:2025
 * protocol. Unlike Proximity (BLE) and Remote (OpenID4VP), DCAPI is triggered by a system
 * intent from the browser rather than user action.
 *
 * Flow:
 * 1. Browser requests credentials via Digital Credentials API
 * 2. Android System sends GET_CREDENTIAL intent to app
 * 3. App processes request and shows to user
 * 4. User approves/denies
 * 5. App returns result intent to browser via setResult()
 */
class DCAPIPresentationViewModel : ViewModel() {

    private val _state = MutableStateFlow<DCAPIState>(DCAPIState.ProcessingIntent)
    val state: StateFlow<DCAPIState> = _state.asStateFlow()

    private val sdk by lazy { ScytalesSdkInitializer.getSdk() }

    private var transferEventListener: TransferEvent.Listener? = null

    // Store the processed request to use when user approves
    private var currentProcessedRequest: RequestProcessor.ProcessedRequest.Success? = null

    companion object {
        private const val TAG = "DCAPIViewModel"
    }

    init {
        Log.d(TAG, "ViewModel initialized for DCAPI presentation")
    }

    /**
     * Start DCAPI presentation with the incoming intent
     *
     * @param intent The GET_CREDENTIAL intent from the system/browser
     */
    fun startDCAPIPresentation(intent: Intent) {
        Log.d(TAG, "Starting DCAPI presentation with intent: ${intent.action}")
        _state.value = DCAPIState.ProcessingIntent

        // Attach transfer event listener
        attachTransferEventListener()

        viewModelScope.launch {
            try {
                // Start DCAPI presentation in SDK
                sdk.startDCAPIPresentation(intent)
                Log.d(TAG, "DCAPI presentation started, waiting for request...")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start DCAPI presentation", e)

                _state.value = DCAPIState.Error(
                    message = "Failed to start presentation: ${e.message}",
                    throwable = e,
                    errorIntent = null
                )
            }
        }
    }

    /**
     * Stop DCAPI presentation and cleanup
     */
    fun stopDCAPIPresentation() {
        Log.d(TAG, "Stopping DCAPI presentation...")

        viewModelScope.launch {
            try {
                // Stop the presentation (works for all presentation types)
                sdk.stopProximityPresentation()
                detachTransferEventListener()
                Log.d(TAG, "DCAPI presentation stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping DCAPI presentation", e)
            }
        }
    }

    /**
     * Approve and send the requested documents
     */
    fun approveRequest(requestedDocuments: List<RequestedDocument>) {
        Log.d(TAG, "User approved DCAPI request for ${requestedDocuments.size} documents")

        val processedRequest = currentProcessedRequest
        if (processedRequest == null) {
            Log.e(TAG, "No processed request available")
            _state.value = DCAPIState.Error(
                message = "No active request to approve",
                throwable = null,
                errorIntent = null
            )
            return
        }

        _state.value = DCAPIState.SendingResponse

        viewModelScope.launch {
            try {
                // Generate response by disclosing all requested items from all requested documents
                val disclosedDocuments = DisclosedDocuments(requestedDocuments.map {
                    DisclosedDocument(
                        documentId = it.documentId,
                        disclosedItems = it.requestedItems.keys.toList()
                    )
                })

                val response = processedRequest.generateResponse(disclosedDocuments).getOrThrow()

                withContext(Dispatchers.IO) {
                    sdk.sendResponse(response)
                }

                Log.d(TAG, "Response generated and sent, waiting for IntentToSend event...")
                // Clear the stored request
                currentProcessedRequest = null

                // Note: State will be updated to ResponseReady when IntentToSend event is received
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send response", e)

                _state.value = DCAPIState.Error(
                    message = "Failed to send response: ${e.message}",
                    throwable = e,
                    errorIntent = null
                )
            }
        }
    }

    /**
     * Deny the request - user cancelled
     */
    fun denyRequest() {
        Log.d(TAG, "DCAPI request denied by user")
        stopDCAPIPresentation()

        // Set error state to trigger activity cancellation
        _state.value = DCAPIState.Error(
            message = "User cancelled the request",
            throwable = null,
            errorIntent = null
        )
    }

    /**
     * Attach transfer event listener to SDK
     */
    private fun attachTransferEventListener() {
        // Remove any existing listeners
        sdk.removeAllTransferEventListeners()

        transferEventListener = TransferEvent.Listener { event ->
            handleTransferEvent(event)
        }

        sdk.addTransferEventListener(transferEventListener!!)
        Log.d(TAG, "Transfer event listener attached")
    }

    /**
     * Detach transfer event listener from SDK
     */
    private fun detachTransferEventListener() {
        transferEventListener?.let {
            sdk.removeTransferEventListener(it)
            Log.d(TAG, "Transfer event listener detached")
        }
    }

    /**
     * Handle transfer events from SDK
     */
    private fun handleTransferEvent(event: TransferEvent) {
        Log.d(TAG, "Transfer event: ${event::class.simpleName}")

        viewModelScope.launch {
            when (event) {
                is TransferEvent.RequestReceived -> {
                    handleRequestReceived(event)
                }

                is TransferEvent.IntentToSend -> {
                    // UNIQUE TO DCAPI - This event provides the intent to return to browser
                    Log.d(TAG, "Intent to send received - response ready")
                    _state.value = DCAPIState.ResponseReady(event.intent)
                }

                is TransferEvent.Error -> {
                    Log.e(TAG, "Transfer error", event.error)
                    _state.value = DCAPIState.Error(
                        message = event.error.message ?: event.error::class.java.simpleName,
                        throwable = event.error,
                        errorIntent = null
                    )
                }

                // Ignore events that are not relevant for DCAPI
                is TransferEvent.QrEngagementReady,
                is TransferEvent.Connecting,
                is TransferEvent.Connected,
                is TransferEvent.ResponseSent,
                is TransferEvent.Redirect,
                is TransferEvent.Disconnected -> {
                    Log.d(TAG, "Ignoring event ${event::class.simpleName} (not relevant for DCAPI)")
                }
            }
        }
    }

    /**
     * Handle request received event
     * Stores the request and waits for user confirmation
     */
    private fun handleRequestReceived(event: TransferEvent.RequestReceived) {
        try {
            val processedRequest = event.processedRequest.getOrThrow()
            val requestedDocuments = processedRequest.requestedDocuments

            Log.d(TAG, "DCAPI request received for ${requestedDocuments.size} documents")

            // Store the processed request for later use when user approves
            currentProcessedRequest = processedRequest.getOrThrow()

            // Extract verifier name if available (browser/app name)
            val verifierName = try {
                requestedDocuments.firstOrNull()?.readerAuth?.readerCommonName
            } catch (e: Exception) {
                Log.w(TAG, "Could not extract verifier name", e)
                null
            }

            // Map requested documents to issued documents
            val documents = requestedDocuments.mapNotNull {
                sdk.getDocumentById(it.documentId) as? IssuedDocument
            }.associateBy { d ->
                requestedDocuments.first { d.id == it.documentId }
            }

            _state.value = DCAPIState.RequestReceived(
                requestedDocuments = documents,
                verifierName = verifierName
            )

            Log.d(TAG, "State updated to RequestReceived, waiting for user approval")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process received request", e)

            _state.value = DCAPIState.Error(
                message = "Failed to process request: ${e.message}",
                throwable = e,
                errorIntent = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, cleaning up...")
        detachTransferEventListener()
        stopDCAPIPresentation()
    }
}

