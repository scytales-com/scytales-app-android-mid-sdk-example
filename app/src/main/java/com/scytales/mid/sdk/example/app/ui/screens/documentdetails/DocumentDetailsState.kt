package com.scytales.mid.sdk.example.app.ui.screens.documentdetails

import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.format.MsoMdocClaim
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcClaim
import org.multipaz.cbor.Cbor
import org.multipaz.cbor.DiagnosticOption
import java.time.Instant

/**
 * UI state for document details screen
 */
sealed class DocumentDetailsState {
    data object Loading : DocumentDetailsState()
    data class Success(val details: DocumentDetails) : DocumentDetailsState()
    data class Error(val message: String) : DocumentDetailsState()
}

/**
 * Detailed information about a document for display
 */
data class DocumentDetails(
    val id: String,
    val name: String,
    val docType: String,
    val format: String,
    val issuedAt: Instant,
    val credentialCount: Int,
    val issuerName: String? = null,
    val additionalInfo: Map<String, String> = emptyMap(),
    val claims: Map<String, Any> = emptyMap()
)

/**
 * Convert IssuedDocument to DocumentDetails
 */
suspend fun IssuedDocument.toDocumentDetails(): DocumentDetails {
    val formatInfo = when (val fmt = this.format) {
        is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> {
            Pair("mso_mdoc", fmt.docType)
        }

        is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> {
            Pair("sd-jwt-vc", fmt.vct)
        }
    }

    // Extract issuer metadata
    val displayMetadata = this.issuerMetadata?.display?.firstOrNull()
    val documentName = displayMetadata?.name

    // Build additional info
    val additionalInfo = mutableMapOf<String, String>()

    // Add any available metadata
    this.issuerMetadata?.let { metadata ->
        additionalInfo["Issuer ID"] = metadata.credentialIssuerIdentifier
    }

    // Extract claims from the document's credentials
    val claims = mutableMapOf<String, Any>()
    data.claims.forEach {
        val name = it.issuerMetadata?.display?.first()?.name ?: it.identifier
        claims[name] = when (it) {
            is MsoMdocClaim -> {
                Cbor.toDiagnostics(
                    it.rawValue, options = setOf(
                        DiagnosticOption.BSTR_PRINT_LENGTH,
                        DiagnosticOption.EMBEDDED_CBOR,
                        DiagnosticOption.PRETTY_PRINT,
                    )
                )
            }

            is SdJwtVcClaim -> {
                it.rawValue
            }
        }
    }

    return DocumentDetails(
        id = this.id,
        name = documentName ?: formatInfo.second,
        docType = formatInfo.second,
        format = formatInfo.first,
        issuedAt = this.issuedAt,
        credentialCount = this.credentialsCount(),
        issuerName = this.issuerMetadata?.issuerDisplay?.firstOrNull()?.name,
        additionalInfo = additionalInfo,
        claims = claims
    )
}

