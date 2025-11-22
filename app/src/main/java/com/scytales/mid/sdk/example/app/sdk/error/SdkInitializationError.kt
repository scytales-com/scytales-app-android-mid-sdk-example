package com.scytales.mid.sdk.example.app.sdk.error

/**
 * Custom exceptions for SDK initialization failures.
 *
 * This sealed class hierarchy provides specific error types for different SDK initialization
 * failures, making it easier to diagnose and handle errors appropriately. Each error type
 * includes a descriptive message and optional cause for debugging.
 *
 * ## Error Types
 * - [InvalidLicense] - License key is missing or malformed
 * - [InvalidOrganization] - Organization URL is invalid or unreachable
 *
 * ## Usage Example
 * ```kotlin
 * lifecycleScope.launch {
 *     val result = ScytalesSdkInitializer.initialize(context)
 *     result.onFailure { error ->
 *         when (error) {
 *             is SdkInitializationError.InvalidLicense -> {
 *                 Log.e(TAG, "License error: ${error.message}")
 *                 showDialog("Please configure a valid license key")
 *             }
 *             is SdkInitializationError.NetworkError -> {
 *                 Log.e(TAG, "Network error: ${error.message}", error.cause)
 *                 showRetryDialog("Network unavailable. Try again?")
 *             }
 *             is SdkInitializationError -> {
 *                 Log.e(TAG, "Initialization error: ${error.message}", error.cause)
 *                 showErrorDialog(error.message ?: "Unknown error")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ## Error Handling Best Practices
 * - Always log the error with its cause for debugging
 * - Provide user-friendly error messages in the UI
 * - Offer retry mechanisms for transient errors (network, storage)
 * - Validate configuration before initialization to prevent errors
 * - Use specific error types to implement targeted recovery strategies
 *
 * @param message Descriptive error message explaining the failure
 * @param cause Optional underlying throwable that caused this error
 *
 * @see ScytalesSdkInitializer.initialize for where these errors are thrown
 * @see SdkState.Failed for the state representation of initialization failure
 */
sealed class SdkInitializationError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Invalid or missing license configuration.
     *
     * This error is thrown when the license key is not properly configured before SDK
     * initialization. It indicates a configuration issue that must be fixed before the
     * SDK can be initialized.
     *
     * ## Common Causes
     * - [SdkConfig.licenseKey] is blank or empty
     * - License key format is malformed
     * - License key was not set before calling [ScytalesSdkInitializer.initialize]
     *
     * ## Resolution
     * 1. Verify that [SdkConfig.licenseKey] is set to a valid license key
     * 2. Check that the license key follows the format: `XXXXX-XXXXX-XXXXX-XXXXX`
     * 3. Ensure the license key is set before initialization
     * 4. Contact Scytales support if you need a valid license key
     *
     * ## Example
     * ```kotlin
     * // Correct configuration
     * SdkConfig.licenseKey = "ABCDE-12345-FGHIJ-67890"
     * val result = ScytalesSdkInitializer.initialize(context)
     * ```
     *
     * @param message Descriptive error message (default provided)
     * @param cause Optional underlying throwable
     *
     * @see SdkConfig.licenseKey
     */
    class InvalidLicense(
        message: String = "Invalid or missing license configuration. Check SdkConfig.licenseKey.",
        cause: Throwable? = null
    ) : SdkInitializationError(message, cause)

    /**
     * Invalid organization configuration.
     *
     * This error is thrown when the organization URL is not properly configured or is
     * unreachable during SDK initialization. The organization URL is required for
     * Scytales Manager features including user signup and credential management.
     *
     * ## Common Causes
     * - [SdkConfig.organizationUrl] is blank or empty
     * - Organization URL format is malformed (not a valid URL)
     * - Organization URL does not point to a valid Scytales Manager instance
     * - Organization does not exist or is not accessible
     * - URL scheme is HTTP instead of HTTPS
     *
     * ## Resolution
     * 1. Verify that [SdkConfig.organizationUrl] is set correctly
     * 2. Ensure the URL uses HTTPS (HTTP is not supported)
     * 3. Check that the URL includes the full path (e.g., `/api/v3`)
     * 4. Verify the organization exists in Scytales Manager
     * 5. Test the URL accessibility from the device
     * 6. Contact your organization administrator for the correct URL
     *
     * ## Example
     * ```kotlin
     * // Correct configuration
     * SdkConfig.organizationUrl = "https://api.your-org.scytales.com/api/v3"
     * val result = ScytalesSdkInitializer.initialize(context)
     * ```
     *
     * @param message Descriptive error message (default provided)
     * @param cause Optional underlying throwable
     *
     * @see SdkConfig.organizationUrl
     */
    class InvalidOrganization(
        message: String = "Invalid organization configuration. Verify SdkConfig.organizationUrl.",
        cause: Throwable? = null
    ) : SdkInitializationError(message, cause)
}

