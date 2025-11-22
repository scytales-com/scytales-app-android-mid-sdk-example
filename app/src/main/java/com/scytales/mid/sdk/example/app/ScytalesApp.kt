package com.scytales.mid.sdk.example.app

import android.app.Application
import android.util.Log
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import kotlinx.coroutines.runBlocking

/**
 * Application class for Scytales MID SDK Example
 */
class ScytalesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application created")

        // Initialize SDK
        initializeSdk()
    }

    private fun initializeSdk() {
        runBlocking {
            Log.d(TAG, "Initializing Scytales MID SDK...")

            ScytalesSdkInitializer.initialize(this@ScytalesApp)
                .onSuccess {
                    Log.d(TAG, "SDK initialized successfully")
                }
                .onFailure { error ->
                    Log.e(TAG, "SDK initialization failed", error)
                }
        }
    }

    companion object {
        private const val TAG = "ScytalesApp"
    }
}

