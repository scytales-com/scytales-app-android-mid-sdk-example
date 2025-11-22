package com.scytales.mid.sdk.example.app.ui.screens.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import com.scytales.mid.sdk.example.app.ui.screens.home.DocumentItem
import com.scytales.mid.sdk.example.app.ui.screens.home.toDocumentItem
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the document list screen
 *
 * Manages loading and displaying IssuedDocument documents from the SDK
 */
class DocumentListViewModel : ViewModel() {

    private val _state = MutableStateFlow<DocumentListState>(DocumentListState.Loading)
    val state: StateFlow<DocumentListState> = _state.asStateFlow()

    init {
        loadDocuments()
    }

    /**
     * Load all IssuedDocument documents from the SDK
     * Can be called externally to refresh the document list
     */
    fun loadDocuments() {
        viewModelScope.launch {
            _state.value = DocumentListState.Loading

            try {
                // Check if SDK is initialized
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = DocumentListState.Error("SDK not initialized. Please restart the app.")
                    return@launch
                }

                // Get SDK instance
                val sdk = ScytalesSdkInitializer.getSdk()

                // Get all documents that are IssuedDocuments
                val documents = sdk.getDocuments { document ->
                    document is IssuedDocument
                }

                Log.d(TAG, "Loaded ${documents.size} documents")

                // Map to UI items
                val documentItems = documents
                    .filterIsInstance<IssuedDocument>()
                    .map { it.toDocumentItem() }

                // Update state
                _state.value = if (documentItems.isEmpty()) {
                    DocumentListState.Empty
                } else {
                    DocumentListState.Success(documentItems)
                }

            } catch (e: IllegalStateException) {
                Log.e(TAG, "SDK not initialized", e)
                _state.value = DocumentListState.Error("SDK not initialized. Please restart the app.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading documents", e)
                _state.value = DocumentListState.Error("Failed to load documents: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "DocumentListViewModel"
    }
}

