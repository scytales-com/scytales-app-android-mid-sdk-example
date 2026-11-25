package com.scytales.mid.sdk.example.app.sdk

import com.scytales.mid.sdk.Sdk
import com.scytales.mid.sdk.manager.signup.isFromSignup
import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.Document

/**
 * The flow that issued a document. Collecting a deferred document and re-issuing a document
 * use the same flow.
 */
enum class DocumentOrigin {
    /** Signup with a Scytales organization: [com.scytales.mid.sdk.manager.signup.SignupManager] and the re-issue manager */
    ScytalesSignup,

    /** OpenID4VCI with the document's credential issuer: [eu.europa.ec.eudi.wallet.issue.openid4vci.OpenId4VciManager] */
    OpenId4Vci
}

/**
 * Which flow issued [document]. A deferred document carries the answer; an issued one came from
 * signup when its issuer is one of the configured Scytales organizations.
 */
suspend fun Sdk.originOf(document: Document): DocumentOrigin = when {
    document is DeferredDocument ->
        if (document.isFromSignup) DocumentOrigin.ScytalesSignup else DocumentOrigin.OpenId4Vci

    getOrganizationForDocument(document) != null -> DocumentOrigin.ScytalesSignup

    else -> DocumentOrigin.OpenId4Vci
}
