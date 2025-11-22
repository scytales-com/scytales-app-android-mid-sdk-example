package com.scytales.mid.sdk.example.app.sdk

import com.scytales.mid.sdk.Sdk

/**
 * Represents the current state of SDK initialization.
 *
 * This sealed class provides a type-safe way to track the lifecycle of the Scytales MID SDK
 * initialization process. It allows UI components and business logic to react appropriately
 * to different initialization states.
 *
 * ## Usage Example
 * ```kotlin
 * // Observing state in a ViewModel
 * val sdkState = ScytalesSdkInitializer.getState()
 * when (sdkState) {
 *     is SdkState.NotInitialized -> {
 *         // Show initial UI, maybe a button to initialize
 *     }
 *     is SdkState.Initializing -> {
 *         // Show loading indicator
 *     }
 *     is SdkState.Initialized -> {
 *         // SDK ready, enable features
 *         val sdk = sdkState.sdk
 *     }
 *     is SdkState.Failed -> {
 *         // Show error message
 *         Log.e(TAG, "SDK failed", sdkState.error)
 *     }
 * }
 * ```
 *
 * ## Thread Safety
 * State changes are managed internally by [ScytalesSdkInitializer] using proper
 * synchronization mechanisms to ensure thread-safe access.
 *
 * @see ScytalesSdkInitializer for SDK initialization management
 * @see ScytalesSdkInitializer.getState to retrieve the current state
 */
sealed class SdkState {
    /**
     * SDK has not been initialized yet.
     *
     * This is the initial state before [ScytalesSdkInitializer.initialize] is called.
     * In this state, the SDK instance is not available and any attempt to access it
     * will result in an exception.
     *
     * ## Typical Actions
     * - Display splash screen or welcome screen
     * - Show initialization button
     * - Validate configuration before attempting initialization
     * - Prepare UI for initialization process
     *
     * ## Next States
     * - [Initializing] when initialization begins
     *
     * @see ScytalesSdkInitializer.initialize to start initialization
     */
    data object NotInitialized : SdkState()

    /**
     * SDK initialization is in progress.
     *
     * This state indicates that [ScytalesSdkInitializer.initialize] has been called and
     * the SDK is currently being set up. This includes license validation, wallet configuration,
     * and manager setup.
     *
     * ## Typical Actions
     * - Display loading indicator or progress spinner
     * - Show "Initializing SDK..." message
     * - Disable user interaction with SDK-dependent features
     * - Optional: Show initialization progress details
     *
     * ## Duration
     * This state typically lasts a few seconds, depending on:
     * - Network connectivity (for license validation)
     * - Device performance
     * - Configuration complexity
     *
     * ## Next States
     * - [Initialized] on successful initialization
     * - [Failed] if initialization encounters an error
     *
     * @see ScytalesSdkInitializer.initialize for the initialization process
     */
    data object Initializing : SdkState()

    /**
     * SDK has been successfully initialized and is ready to use.
     *
     * This state indicates that the SDK initialization completed successfully and all
     * SDK features are now available. The [sdk] property provides direct access to the
     * initialized SDK instance.
     *
     * ## Typical Actions
     * - Hide loading indicators
     * - Enable SDK-dependent features in the UI
     * - Access wallet, manager, and other SDK components
     * - Begin credential issuance or presentation flows
     * - Navigate to main application screen
     *
     * ## Available Features
     * When in this state, you can access:
     * - Wallet operations ([sdk.wallet])
     * - Document management ([sdk.wallet.documentManager])
     * - Scytales Manager features ([sdk.manager])
     * - Credential issuance and presentation
     *
     * ## Usage Example
     * ```kotlin
     * when (val state = ScytalesSdkInitializer.getState()) {
     *     is SdkState.Initialized -> {
     *         val wallet = state.sdk.wallet
     *         val manager = state.sdk.manager
     *         // Use SDK features
     *     }
     *     else -> { /* Handle other states */ }
     * }
     * ```
     *
     * @property sdk The initialized [Sdk] instance ready for use
     *
     * @see Sdk for available SDK functionality
     * @see ScytalesSdkInitializer.getSdk for alternative access method
     */
    data class Initialized(val sdk: Sdk) : SdkState()

    /**
     * SDK initialization failed.
     *
     * This state indicates that the SDK initialization encountered an error and could not
     * complete successfully. The [error] property contains the throwable that caused the
     * failure, which can be analyzed to determine the root cause.
     *
     * ## Typical Actions
     * - Hide loading indicators
     * - Display error message to user
     * - Log error for debugging
     * - Offer retry option
     * - Disable SDK-dependent features
     * - Show fallback UI or offline mode
     *
     * ## Common Error Types
     * The error can be one of several [SdkInitializationError] types:
     * - [SdkInitializationError.InvalidLicense] - License key is missing or invalid
     * - [SdkInitializationError.LicenseValidationError] - License validation failed
     * - [SdkInitializationError.InvalidOrganization] - Organization URL is invalid
     * - [SdkInitializationError.StorageError] - Storage setup failed
     * - [SdkInitializationError.NetworkError] - Network connectivity issues
     * - [SdkInitializationError.Unknown] - Other unexpected errors
     *
     * ## Error Handling Example
     * ```kotlin
     * when (val state = ScytalesSdkInitializer.getState()) {
     *     is SdkState.Failed -> {
     *         Log.e(TAG, "SDK initialization failed", state.error)
     *         when (state.error) {
     *             is SdkInitializationError.NetworkError -> {
     *                 // Show network error message, offer retry
     *                 showRetryDialog("Network error. Please check connection.")
     *             }
     *             is SdkInitializationError.InvalidLicense -> {
     *                 // Show license error, possibly with config option
     *                 showErrorDialog("Invalid license key.")
     *             }
     *             else -> {
     *                 // Show generic error
     *                 showErrorDialog("Initialization failed: ${state.error.message}")
     *             }
     *         }
     *     }
     *     else -> { /* Handle other states */ }
     * }
     * ```
     *
     * ## Recovery Options
     * After a failure, you can:
     * - Fix configuration issues (if configuration-related)
     * - Call [ScytalesSdkInitializer.reset] to clear the state
     * - Call [ScytalesSdkInitializer.initialize] again to retry
     * - Check network connectivity and retry
     * - Contact support if the error persists
     *
     * @property error The [Throwable] that caused initialization to fail. May be a specific
     *                 [SdkInitializationError] subtype with additional context.
     *
     * @see SdkInitializationError for specific error types
     * @see ScytalesSdkInitializer.reset to clear failed state
     * @see ScytalesSdkInitializer.initialize to retry initialization
     */
    data class Failed(val error: Throwable) : SdkState()
}

