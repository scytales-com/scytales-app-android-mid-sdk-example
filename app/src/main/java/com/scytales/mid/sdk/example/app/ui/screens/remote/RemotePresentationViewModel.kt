package com.scytales.mid.sdk.example.app.ui.screens.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
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
 * ViewModel for Remote Presentation (OpenID4VP) flow
 *
 * Manages the lifecycle of remote document presentation via HTTPS
 */
class RemotePresentationViewModel : ViewModel() {

    companion object {
        private const val TAG = "RemoteViewModel"
    }

    private val _state = MutableStateFlow<RemoteState>(RemoteState.Idle)
    val state: StateFlow<RemoteState> = _state.asStateFlow()

    private val sdk by lazy { ScytalesSdkInitializer.getSdk() }

    private var transferEventListener: TransferEvent.Listener? = null

    // Store the processed request to use when user approves
    private var currentProcessedRequest: RequestProcessor.ProcessedRequest.Success? = null

    /**
     * Start remote presentation with the given request URI
     *
     * @param uri The OpenID4VP request URI from deep link
     */
    fun startRemotePresentation(uri: Uri) {
        if (_state.value !is RemoteState.Idle) {
            Log.w(TAG, "Remote presentation already in progress")
            return
        }

        Log.d(TAG, "Starting remote presentation with URI: $uri")
        _state.value = RemoteState.ProcessingRequest

        // Attach transfer event listener
        attachTransferEventListener()

        viewModelScope.launch {
            try {
                // Start remote presentation
                withContext(Dispatchers.IO) {
                    sdk.startRemotePresentation(uri)
                }

                Log.d(TAG, "Remote presentation started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start remote presentation", e)
                _state.value = RemoteState.Error(
                    message = "Failed to start remote presentation: ${e.message}",
                    throwable = e
                )
            }
        }
    }

    /**
     * Stop remote presentation and cleanup
     */
    fun stopRemotePresentation() {
        Log.d(TAG, "Stopping remote presentation...")

        viewModelScope.launch {
            try {
                sdk.stopRemotePresentation()
                detachTransferEventListener()
                _state.value = RemoteState.Idle
                currentProcessedRequest = null
                Log.d(TAG, "Remote presentation stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping remote presentation", e)
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
            _state.value = RemoteState.Error(
                message = "No active request to approve",
                throwable = null
            )
            return
        }

        _state.value = RemoteState.SendingResponse

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
                _state.value = RemoteState.Error(
                    message = "Failed to send response: ${e.message}",
                    throwable = e
                )
            }
        }
    }

    /**
     * Deny the request and stop presentation
     */
    fun denyRequest() {
        Log.d(TAG, "User denied request")
        stopRemotePresentation()
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
     * Handle transfer events from the SDK
     */
    private fun handleTransferEvent(event: TransferEvent) {
        Log.d(TAG, "Transfer event: ${event::class.simpleName}")

        viewModelScope.launch {
            when (event) {
                // Remote presentation doesn't have QrEngagementReady, Connecting, Connected
                // These are only for proximity (BLE) presentation

                is TransferEvent.RequestReceived -> {
                    handleRequestReceived(event)
                }

                is TransferEvent.ResponseSent -> {
                    Log.d(TAG, "Response sent successfully")
                    _state.value = RemoteState.ResponseSent
                }

                is TransferEvent.Redirect -> {
                    // Important: OpenID4VP specific - handle redirect
                    Log.d(TAG, "Redirect required: ${event.redirectUri}")
                    _state.value = RemoteState.Redirect(event.redirectUri.toString().toUri())
                }

                is TransferEvent.Disconnected -> {
                    Log.d(TAG, "Disconnected")
                    stopRemotePresentation()
                }

                is TransferEvent.Error -> {
                    Log.e(TAG, "Transfer error", event.error)
                    _state.value = RemoteState.Error(
                        message = event.error.message ?: event.error::class.java.simpleName,
                        throwable = event.error
                    )

                    // Don't call stopRemotePresentation() to keep error visible
                    detachTransferEventListener()
                    currentProcessedRequest = null
                }

                is TransferEvent.QrEngagementReady -> {
                    Log.d(TAG, "QR engagement - not applicable for remote")
                    // This event is only for proximity presentation
                }

                is TransferEvent.Connecting -> {
                    Log.d(TAG, "Connecting - not applicable for remote")
                    // This event is only for proximity presentation
                }

                is TransferEvent.Connected -> {
                    Log.d(TAG, "Connected - not applicable for remote")
                    // This event is only for proximity presentation
                }

                is TransferEvent.IntentToSend -> {
                    Log.d(TAG, "Intent to send: ${event.intent}")
                    // This event may be triggered for remote presentation
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

            // Look up the actual IssuedDocument instances from SDK
            val documents = requestedDocuments.mapNotNull {
                sdk.getDocumentById(it.documentId) as? IssuedDocument
            }.associateBy { d ->
                requestedDocuments.first { d.id == it.documentId }
            }

            // Show the request to user for approval/denial
            _state.value = RemoteState.RequestReceived(
                requestedDocuments = documents,
                verifierName = verifierName
            )

            Log.d(TAG, "Waiting for user confirmation...")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process request", e)
            _state.value = RemoteState.Error(
                message = "Failed to process request: ${e.message}",
                throwable = e
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, stopping presentation")
        stopRemotePresentation()
    }
}


