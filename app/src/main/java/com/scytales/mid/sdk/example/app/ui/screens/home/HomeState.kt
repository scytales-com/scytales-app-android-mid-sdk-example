package com.scytales.mid.sdk.example.app.ui.screens.home

import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.format.DocumentFormat
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
 *
 * @property isPending true for a deferred document: the issuer has not decided yet
 * @property credentialPolicy how the batch's credentials are used, e.g. RotatingBatch or OnceOnly
 */
data class DocumentItem(
    val id: String,
    val name: String,
    val docType: String,
    val format: String,
    val createdAt: Instant,
    val credentialCount: Int,
    val isPending: Boolean = false,
    val credentialPolicy: String? = null
)

/**
 * Map IssuedDocument to DocumentItem
 */
suspend fun IssuedDocument.toDocumentItem(): DocumentItem {
    val formatInfo = format.toFormatInfo()

    return DocumentItem(
        id = this.id,
        name = this.issuerMetadata?.display?.firstOrNull()?.name ?: formatInfo.second,
        docType = formatInfo.second,
        format = formatInfo.first,
        createdAt = this.createdAt,
        credentialCount = this.credentialsCount(),
        credentialPolicy = this.credentialPolicy::class.simpleName
    )
}

/**
 * Map a DeferredDocument, which has no credentials yet, to a pending DocumentItem
 */
fun DeferredDocument.toPendingDocumentItem(): DocumentItem {
    val formatInfo = format.toFormatInfo()

    return DocumentItem(
        id = this.id,
        name = this.issuerMetadata?.display?.firstOrNull()?.name ?: formatInfo.second,
        docType = formatInfo.second,
        format = formatInfo.first,
        createdAt = this.createdAt,
        credentialCount = 0,
        isPending = true
    )
}

private fun DocumentFormat.toFormatInfo(): Pair<String, String> = when (this) {
    is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> Pair("mso_mdoc", docType)
    is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> Pair("sd-jwt-vc", vct)
}

