package com.scytales.mid.sdk.example.app.ui.screens.remote

import android.net.Uri
import eu.europa.ec.eudi.iso18013.transfer.response.RequestedDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument

/**
 * Represents the state of remote presentation (OpenID4VP) flow
 */
sealed class RemoteState {
    /**
     * Initial state - no active presentation
     */
    object Idle : RemoteState()

    /**
     * Processing the incoming request URI
     */
    object ProcessingRequest : RemoteState()

    /**
     * Request received and processed - waiting for user decision
     *
     * @property requestedDocuments Map of requested documents to issued documents
     * @property verifierName Optional name of the verifier
     */
    data class RequestReceived(
        val requestedDocuments: Map<RequestedDocument, IssuedDocument>,
        val verifierName: String? = null
    ) : RemoteState()

    /**
     * Sending response to verifier
     */
    object SendingResponse : RemoteState()

    /**
     * Response sent successfully
     */
    object ResponseSent : RemoteState()

    /**
     * Redirect required (unique to OpenID4VP)
     *
     * @property redirectUri The URI to redirect to
     */
    data class Redirect(val redirectUri: Uri) : RemoteState()

    /**
     * An error occurred during the presentation
     *
     * @property message Error message to display
     * @property throwable Optional exception for logging
     */
    data class Error(
        val message: String,
        val throwable: Throwable?
    ) : RemoteState()
}

