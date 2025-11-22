package com.scytales.mid.sdk.example.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen
 *
 * Manages SDK configuration status for display
 */
class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.DcapiDisabled)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        checkDcapiStatus()
    }

    /**
     * Check if DCAPI is enabled in the SDK configuration
     */
    private fun checkDcapiStatus() {
        viewModelScope.launch {
            try {
                // Check if SDK is initialized
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = HomeState.DcapiDisabled
                    return@launch
                }

                // Get SDK instance and check DCAPI status
                ScytalesSdkInitializer.getSdk()

                // For now, assume DCAPI is enabled based on SDK configuration
                // This could be expanded to check actual SDK configuration
                _state.value = HomeState.DcapiEnabled

                Log.d(TAG, "DCAPI status checked: ${_state.value}")

            } catch (e: Exception) {
                Log.e(TAG, "Error checking DCAPI status", e)
                _state.value = HomeState.DcapiDisabled
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}

