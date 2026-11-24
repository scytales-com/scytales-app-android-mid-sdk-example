package com.scytales.mid.sdk.example.app.ui.screens.manager

import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument

/**
 * UI state for document issuance flow
 */
sealed class IssuanceState {
    /**
     * No issuance in progress
     */
    data object Idle : IssuanceState()

    /**
     * Document issuance in progress
     */
    data object Issuing : IssuanceState()

    /**
     * Document issued successfully
     */
    data class Success(val document: IssuedDocument) : IssuanceState()

    /**
     * The issuer has not decided yet: the document is stored as pending and collected later
     */
    data class Deferred(val document: DeferredDocument) : IssuanceState()

    /**
     * Document issuance failed
     */
    data class Error(val message: String, val error: Throwable) : IssuanceState()
}

