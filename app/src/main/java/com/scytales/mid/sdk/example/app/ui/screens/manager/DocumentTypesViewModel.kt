package com.scytales.mid.sdk.example.app.ui.screens.manager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the document types screen
 *
 * Manages loading available document types from the SDK
 */
class DocumentTypesViewModel : ViewModel() {

    private val _state = MutableStateFlow<DocumentTypesState>(DocumentTypesState.Loading)
    val state: StateFlow<DocumentTypesState> = _state.asStateFlow()

    init {
        loadDocumentTypes()
    }

    /**
     * Load available document types from the SDK
     */
    fun loadDocumentTypes() {
        viewModelScope.launch {
            _state.value = DocumentTypesState.Loading

            try {
                // Check if SDK is initialized
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = DocumentTypesState.Error("SDK not initialized. Please restart the app.")
                    return@launch
                }

                // Get SDK instance
                val sdk = ScytalesSdkInitializer.getSdk()

                // Get available document types
                val result = sdk.getAvailableDocumentTypes()

                result.fold(
                    onSuccess = { documentTypes ->
                        Log.d(TAG, "Loaded ${documentTypes.size} document types")

                        // Map to UI items
                        val items = documentTypes.map { it.toDocumentTypeItem() }

                        // Update state
                        _state.value = if (items.isEmpty()) {
                            DocumentTypesState.Empty
                        } else {
                            DocumentTypesState.Success(items)
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Error loading document types", error)
                        _state.value = DocumentTypesState.Error(
                            "Failed to load document types: ${error.message}"
                        )
                    }
                )

            } catch (e: IllegalStateException) {
                Log.e(TAG, "SDK not initialized", e)
                _state.value = DocumentTypesState.Error("SDK not initialized. Please restart the app.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading document types", e)
                _state.value = DocumentTypesState.Error("Failed to load document types: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "DocumentTypesViewModel"
    }
}

