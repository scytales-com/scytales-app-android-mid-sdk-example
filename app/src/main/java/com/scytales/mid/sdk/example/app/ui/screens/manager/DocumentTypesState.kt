package com.scytales.mid.sdk.example.app.ui.screens.manager

import com.scytales.mid.sdk.manager.AvailableDocumentType
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat

/**
 * UI state for the document types screen
 */
sealed class DocumentTypesState {
    /**
     * Loading available document types
     */
    data object Loading : DocumentTypesState()

    /**
     * Document types loaded successfully
     */
    data class Success(val documentTypes: List<DocumentTypeItem>) : DocumentTypesState()

    /**
     * No document types available
     */
    data object Empty : DocumentTypesState()

    /**
     * Error loading document types
     */
    data class Error(val message: String) : DocumentTypesState()
}

/**
 * UI-friendly representation of a document type
 */
data class DocumentTypeItem(
    val id: String,
    val name: String,
    val docType: String,
    val format: String,
    val description: String?,
    val availableDocumentType: AvailableDocumentType
)

/**
 * Map AvailableDocumentType to DocumentTypeItem
 */
fun AvailableDocumentType.toDocumentTypeItem(): DocumentTypeItem {
    val (formatName, docType) = when (val fmt = this.format) {
        is MsoMdocFormat -> Pair("mso_mdoc", fmt.docType)
        is SdJwtVcFormat -> Pair("sd-jwt-vc", fmt.vct)
    }

    val displayName = this.template.display.first().name

    return DocumentTypeItem(
        id = docType,
        name = displayName,
        docType = docType,
        format = formatName,
        description = this.template.display.first().category?.name,
        availableDocumentType = this
    )
}

