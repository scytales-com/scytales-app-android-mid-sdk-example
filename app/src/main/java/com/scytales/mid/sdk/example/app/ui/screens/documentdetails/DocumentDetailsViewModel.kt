package com.scytales.mid.sdk.example.app.ui.screens.documentdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for document details screen
 */
class DocumentDetailsViewModel(
    private val documentId: String
) : ViewModel() {

    private val _state = MutableStateFlow<DocumentDetailsState>(DocumentDetailsState.Loading)
    val state: StateFlow<DocumentDetailsState> = _state.asStateFlow()

    init {
        loadDocumentDetails()
    }

    fun loadDocumentDetails() {
        viewModelScope.launch {
            _state.value = DocumentDetailsState.Loading

            try {
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = DocumentDetailsState.Error("SDK not initialized")
                    return@launch
                }

                val sdk = ScytalesSdkInitializer.getSdk()
                val document = sdk.getDocumentById(documentId) as? IssuedDocument

                if (document == null) {
                    _state.value = DocumentDetailsState.Error("Document not found")
                    return@launch
                }

                val details = document.toDocumentDetails()
                _state.value = DocumentDetailsState.Success(details)

            } catch (e: Exception) {
                Log.e(TAG, "Error loading document details", e)
                _state.value = DocumentDetailsState.Error("Failed to load document: ${e.message}")
            }
        }
    }

    fun deleteDocument() {
        viewModelScope.launch {
            try {
                if (!ScytalesSdkInitializer.isInitialized()) {
                    return@launch
                }

                val sdk = ScytalesSdkInitializer.getSdk()
                sdk.deleteDocumentById(documentId)
                Log.d(TAG, "Document deleted: $documentId")

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting document", e)
            }
        }
    }

    companion object {
        private const val TAG = "DocumentDetailsViewModel"
    }
}

/**
 * Factory for creating DocumentDetailsViewModel with documentId parameter
 */
class DocumentDetailsViewModelFactory(
    private val documentId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocumentDetailsViewModel::class.java)) {
            return DocumentDetailsViewModel(documentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

