package com.scytales.mid.sdk.example.app.ui.screens.proximity

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
 * ViewModel for managing proximity presentation state and events
 */
class ProximityPresentationViewModel : ViewModel() {

    private val _state = MutableStateFlow<ProximityState>(ProximityState.Idle)
    val state: StateFlow<ProximityState> = _state.asStateFlow()

    private val sdk by lazy { ScytalesSdkInitializer.getSdk() }

    private var transferEventListener: TransferEvent.Listener? = null

    // Store the processed request to use when user approves
    private var currentProcessedRequest: RequestProcessor.ProcessedRequest.Success? = null

    companion object {
        private const val TAG = "ProximityViewModel"
        private const val QR_CODE_SIZE = 800
    }

    init {
        Log.d(TAG, "ViewModel initialized")
    }

    /**
     * Start proximity presentation - generates QR code and waits for BLE connection
     */
    fun startProximityPresentation() {
        Log.d(TAG, "Starting proximity presentation...")
        _state.value = ProximityState.Initializing

        // Attach transfer event listener
        attachTransferEventListener()

        viewModelScope.launch {
            try {
                // Start proximity presentation in SDK
                sdk.startProximityPresentation()
                Log.d(TAG, "Proximity presentation started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start proximity presentation", e)
                _state.value = ProximityState.Error(
                    message = "Failed to start presentation: ${e.message}",
                    throwable = e
                )
            }
        }
    }

    /**
     * Stop proximity presentation and cleanup
     */
    fun stopProximityPresentation() {
        Log.d(TAG, "Stopping proximity presentation...")

        viewModelScope.launch {
            try {
                sdk.stopProximityPresentation()
                detachTransferEventListener()
                _state.value = ProximityState.Idle
                Log.d(TAG, "Proximity presentation stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping proximity presentation", e)
            }
        }
    }

    /**
     * Approve and send the requested documents
     */
    fun approveRequest(requestedDocuments: List<RequestedDocument>) {
        Log.d(TAG, "User approved request for ${requestedDocuments.size} documents")

        val processedRequest = currentProcessedRequest
        if (processedRequest == null) {
            Log.e(TAG, "No processed request available")
            _state.value = ProximityState.Error(
                message = "No active request to approve",
                throwable = null
            )
            return
        }

        _state.value = ProximityState.SendingResponse

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

                Log.d(TAG, "Response generated and sent after user approval")
                // Clear the stored request
                currentProcessedRequest = null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send response", e)
                _state.value = ProximityState.Error(
                    message = "Failed to send response: ${e.message}",
                    throwable = e
                )
            }
        }
    }

    /**
     * Deny the request
     */
    fun denyRequest() {
        Log.d(TAG, "Request denied by user")
        stopProximityPresentation()
    }

    /**
     * Attach transfer event listener to SDK
     */
    private fun attachTransferEventListener() {
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
                is TransferEvent.QrEngagementReady -> {
                    Log.d(TAG, "QR code ready")
                    val qrBitmap = event.qrCode.asBitmap(size = QR_CODE_SIZE)
                    _state.value = ProximityState.QrReady(qrBitmap)
                }

                is TransferEvent.Connecting -> {
                    Log.d(TAG, "Verifier connecting...")
                    _state.value = ProximityState.Connecting
                }

                is TransferEvent.Connected -> {
                    Log.d(TAG, "Verifier connected")
                    _state.value = ProximityState.Connected
                }

                is TransferEvent.RequestReceived -> {
                    handleRequestReceived(event)
                }

                is TransferEvent.ResponseSent -> {
                    Log.d(TAG, "Response sent successfully")
                    _state.value = ProximityState.ResponseSent
                }

                is TransferEvent.Redirect -> {
                    Log.d(TAG, "Redirect URI: ${event.redirectUri}")
                    // For proximity presentation, redirect is not typically used
                }

                is TransferEvent.Disconnected -> {
                    Log.d(TAG, "Verifier disconnected")
                    _state.value = ProximityState.Disconnected
                    stopProximityPresentation()
                }

                is TransferEvent.Error -> {
                    Log.e(TAG, "Transfer error", event.error)
                    _state.value = ProximityState.Error(
                        message = event.error.message ?: event.error::class.java.simpleName,
                        throwable = event.error
                    )

                    // Don't call stopProximityPresentation() to keep error visible
                    detachTransferEventListener()
                    currentProcessedRequest = null
                }

                is TransferEvent.IntentToSend -> {
                    Log.d(TAG, "Intent to send - not applicable for proximity")
                    // This event is only for remote presentation (OpenID4VP)
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

            Log.d(TAG, "Request received for ${requestedDocuments.size} documents")

            // Store the processed request for later use when user approves
            currentProcessedRequest = processedRequest.getOrThrow()

            // Extract verifier name if available
            val verifierName = try {
                requestedDocuments.firstOrNull()?.readerAuth?.readerCommonName
            } catch (e: Exception) {
                Log.w(TAG, "Could not extract verifier name", e)
                null
            }

            val documents = requestedDocuments.mapNotNull {
                sdk.getDocumentById(it.documentId) as? IssuedDocument
            }.associateBy { d ->
                requestedDocuments.first { d.id == it.documentId }
            }
            // Show the request to user for approval/denial
            _state.value = ProximityState.RequestReceived(
                requestedDocuments = documents,
                verifierName = verifierName
            )

            Log.d(TAG, "Waiting for user confirmation...")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process request", e)
            _state.value = ProximityState.Error(
                message = "Failed to process request: ${e.message}",
                throwable = e
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, stopping presentation")
        stopProximityPresentation()
    }
}

