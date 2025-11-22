package com.scytales.mid.sdk.example.app.ui.screens.home

import eu.europa.ec.eudi.wallet.document.IssuedDocument
import java.time.Instant

/**
 * UI state for the home screen
 */
sealed class HomeState {
    /**
     * DCAPI is enabled
     */
    data object DcapiEnabled : HomeState()

    /**
     * DCAPI is disabled
     */
    data object DcapiDisabled : HomeState()
}

/**
 * UI-friendly representation of a document
 */
data class DocumentItem(
    val id: String,
    val name: String,
    val docType: String,
    val format: String,
    val createdAt: Instant,
    val credentialCount: Int
)

/**
 * Map IssuedDocument to DocumentItem
 */
suspend fun IssuedDocument.toDocumentItem(): DocumentItem {
    val formatInfo = when (val fmt = this.format) {
        is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> {
            Pair("mso_mdoc", fmt.docType)
        }
        is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> {
            Pair("sd-jwt-vc", fmt.vct)
        }
    }

    return DocumentItem(
        id = this.id,
        name = this.issuerMetadata?.display?.first()?.name ?: formatInfo.second,
        docType = formatInfo.second,
        format = formatInfo.first,
        createdAt = this.createdAt,
        credentialCount = this.credentialsCount()
    )
}

