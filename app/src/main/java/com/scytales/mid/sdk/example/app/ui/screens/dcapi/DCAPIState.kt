package com.scytales.mid.sdk.example.app.ui.screens.dcapi

import android.content.Intent
import eu.europa.ec.eudi.iso18013.transfer.response.RequestedDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument

/**
 * Represents the state of DCAPI (Digital Credentials API) presentation flow
 *
 * DCAPI is browser-initiated credential verification using Android's credential provider system.
 * Unlike Proximity (BLE) and Remote (OpenID4VP), DCAPI is triggered by a system intent
 * rather than user action.
 */
sealed class DCAPIState {
    /**
     * Processing the incoming DCAPI intent
     * This is the initial state when the app receives a GET_CREDENTIAL intent
     */
    data object ProcessingIntent : DCAPIState()

    /**
     * Request received and processed - waiting for user decision
     *
     * @property requestedDocuments Map of requested documents to issued documents
     * @property verifierName Optional name of the verifier/browser
     */
    data class RequestReceived(
        val requestedDocuments: Map<RequestedDocument, IssuedDocument>,
        val verifierName: String? = null
    ) : DCAPIState()

    /**
     * Sending response back to the browser via system intent
     */
    data object SendingResponse : DCAPIState()

    /**
     * Response ready - contains the intent to return to the browser
     * This state triggers the activity to finish with result
     *
     * @property intent The result intent to return to the browser/system
     */
    data class ResponseReady(val intent: Intent) : DCAPIState()

    /**
     * An error occurred during the DCAPI presentation
     *
     * @property message Error message to display to the user
     * @property throwable Optional exception for debugging
     * @property errorIntent Optional error intent for DCAPIException cases
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
        val errorIntent: Intent? = null
    ) : DCAPIState()
}

