package com.scytales.mid.sdk.example.app.presentation

import com.scytales.mid.sdk.Sdk
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import org.multipaz.presentment.CredentialPresentmentSelection

/** A document the verifier asked for, as the consent screens display it. */
data class RequestedDocumentInfo(
    val documentName: String,
    val claims: List<String>
)

/** Projects a selection into consent rows, shared by the proximity, remote and DCAPI screens. */
fun CredentialPresentmentSelection.toRequestedDocuments(sdk: Sdk): List<RequestedDocumentInfo> =
    matches.map { match ->
        val documentId = match.credential.document.identifier
        val issuedDocument = sdk.getDocumentById(documentId) as? IssuedDocument

        RequestedDocumentInfo(
            documentName = issuedDocument?.name ?: documentId,
            claims = match.claims.values.map { it.displayName }
        )
    }
