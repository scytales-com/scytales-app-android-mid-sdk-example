package com.scytales.mid.sdk.example.app.ui.screens.documents

import com.scytales.mid.sdk.example.app.ui.screens.home.DocumentItem

/**
 * UI state for the document list screen
 */
sealed class DocumentListState {
    /**
     * Loading documents
     */
    data object Loading : DocumentListState()

    /**
     * Documents loaded successfully
     */
    data class Success(val documents: List<DocumentItem>) : DocumentListState()

    /**
     * No documents available
     */
    data object Empty : DocumentListState()

    /**
     * Error loading documents
     */
    data class Error(val message: String) : DocumentListState()
}

