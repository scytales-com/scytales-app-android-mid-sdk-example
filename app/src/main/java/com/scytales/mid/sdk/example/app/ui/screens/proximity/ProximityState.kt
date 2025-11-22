package com.scytales.mid.sdk.example.app.ui.screens.proximity

import android.graphics.Bitmap
import eu.europa.ec.eudi.iso18013.transfer.response.RequestedDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument

/**
 * Represents the state of proximity presentation
 */
sealed class ProximityState {
    /**
     * Initial idle state - ready to start
     */
    data object Idle : ProximityState()

    /**
     * Initializing proximity presentation
     */
    data object Initializing : ProximityState()

    /**
     * QR code is ready to be displayed
     */
    data class QrReady(val qrBitmap: Bitmap) : ProximityState()

    /**
     * Verifier is connecting via BLE
     */
    data object Connecting : ProximityState()

    /**
     * BLE connection established with verifier
     */
    data object Connected : ProximityState()

    /**
     * Document request received from verifier
     */
    data class RequestReceived(
        val requestedDocuments: Map<RequestedDocument, IssuedDocument>,
        val verifierName: String? = null
    ) : ProximityState()

    /**
     * Sending response to verifier
     */
    data object SendingResponse : ProximityState()

    /**
     * Response successfully sent
     */
    data object ResponseSent : ProximityState()

    /**
     * Connection disconnected
     */
    data object Disconnected : ProximityState()

    /**
     * Error occurred during transfer
     */
    data class Error(val message: String, val throwable: Throwable? = null) : ProximityState()
}

