package com.scytales.mid.sdk.example.app.ui.screens.manager

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import com.scytales.mid.sdk.manager.AvailableDocumentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling document issuance
 */
class DocumentIssuanceViewModel : ViewModel() {

    private val _issuanceState = MutableStateFlow<IssuanceState>(IssuanceState.Idle)
    val issuanceState: StateFlow<IssuanceState> = _issuanceState.asStateFlow()

    /**
     * Issue a document using the Scytales Manager
     */
    fun issueDocument(
        activity: ComponentActivity,
        documentType: AvailableDocumentType
    ) {
        viewModelScope.launch {
            _issuanceState.value = IssuanceState.Issuing

            Log.d(TAG, "Starting document issuance for: ${documentType.format}")

            try {
                // Check if SDK is initialized
                if (!ScytalesSdkInitializer.isInitialized()) {
                    val error = IllegalStateException("SDK not initialized")
                    _issuanceState.value = IssuanceState.Error(
                        "SDK not initialized. Please restart the app.",
                        error
                    )
                    return@launch
                }

                // Get SDK instance
                val sdk = ScytalesSdkInitializer.getSdk()

                // Create SignupManager
                val signupManager = sdk.createSignManager()

                Log.d(TAG, "Issuing document: ${documentType.format}")

                // Issue the document
                val result = signupManager.issueDocument(activity, documentType)

                result.fold(
                    onSuccess = { issuedDocument ->
                        Log.d(TAG, "Document issued successfully: ${issuedDocument.id}")
                        _issuanceState.value = IssuanceState.Success(issuedDocument)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Document issuance failed", error)
                        _issuanceState.value = IssuanceState.Error(
                            "Failed to issue document: ${error.message ?: "Unknown error"}",
                            error
                        )
                    }
                )

            } catch (e: Exception) {
                Log.e(TAG, "Exception during document issuance", e)
                _issuanceState.value = IssuanceState.Error(
                    "Error issuing document: ${e.message ?: "Unknown error"}",
                    e
                )
            }
        }
    }

    /**
     * Reset issuance state to idle
     */
    fun resetState() {
        _issuanceState.value = IssuanceState.Idle
    }

    companion object {
        private const val TAG = "DocumentIssuanceVM"
    }
}

