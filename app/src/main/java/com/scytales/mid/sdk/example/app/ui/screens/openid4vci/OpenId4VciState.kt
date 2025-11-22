package com.scytales.mid.sdk.example.app.ui.screens.openid4vci

import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer

/**
 * UI display utilities for OpenID4VCI
 *
 * Note: State classes (OfferState, IssuanceProgress) are defined in OpenId4VciViewModel.kt
 * This file contains only display/conversion utilities.
 */

/**
 * UI-friendly representation of a credential offer
 */
data class OfferDisplay(
    val issuerName: String,
    val documents: List<OfferedDocumentDisplay>,
    val requiresTxCode: Boolean,
    val txCodeDescription: String?
)

/**
 * UI-friendly representation of an offered document
 */
data class OfferedDocumentDisplay(
    val name: String,
    val docType: String,
    val format: String
)

/**
 * Convert Offer to OfferDisplay
 */
fun Offer.toOfferDisplay(): OfferDisplay {
    val documents = this.offeredDocuments.map { it.toOfferedDocumentDisplay() }

    return OfferDisplay(
        issuerName = this.issuerMetadata.display.first().name,
        documents = documents,
        requiresTxCode = this.txCodeSpec != null,
        txCodeDescription = this.txCodeSpec?.description
    )
}

/**
 * Convert OfferedDocument to OfferedDocumentDisplay
 */
fun Offer.OfferedDocument.toOfferedDocumentDisplay(): OfferedDocumentDisplay {
    val format = when (val fmt = this.documentFormat) {
        is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> {
            Pair("mso_mdoc", fmt.docType)
        }

        is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> {
            Pair("sd-jwt-vc", fmt.vct)
        }

        else -> Pair("unknown", "unknown")
    }

    return OfferedDocumentDisplay(
        name = this.configuration.credentialMetadata?.display?.first()?.name ?: format.second,
        docType = format.second,
        format = format.first
    )
}

