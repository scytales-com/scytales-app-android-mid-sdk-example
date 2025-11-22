package com.scytales.mid.sdk.example.app.sdk

/**
 * Configuration object for Scytales MID SDK initialization.
 *
 * This object holds all configuration parameters required for initializing the Scytales MID SDK.
 * It uses compile-time constants for required configuration and nullable properties for optional
 * features like FaceTec biometric enrollment.
 *
 * ## Configuration Categories
 * - **License** (required): SDK license key for authentication
 * - **Organization** (required): Organization URL for Scytales Manager features
 * - **OIDC Redirect** (required): Redirect URI for OpenID Connect signup flow
 * - **FaceTec** (optional): Biometric enrollment configuration
 *
 * ## Usage
 * All required configurations are set as constants in this object. Before deploying to production,
 * ensure these values are configured correctly:
 *
 * ```kotlin
 * // Required configurations (set as constants)
 * SdkConfig.licenseKey            // Your Scytales license key
 * SdkConfig.organizationUrl       // Your organization API URL
 * SdkConfig.singupOidcRedirectUri // OIDC redirect URI
 *
 * // Optional configurations (set at runtime if needed)
 * SdkConfig.faceTecDeviceKey = "your-device-key"
 * SdkConfig.faceTecPublicKey = "your-public-key"
 *
 * // Initialize SDK
 * ScytalesSdkInitializer.initialize(context)
 * ```
 *
 * ## Security Considerations
 * - **License Key**: Should be kept secure and not exposed in version control for production apps.
 *   Consider using BuildConfig or secure storage mechanisms for production builds.
 * - **Organization URL**: Must match your organization's API endpoint configured in Scytales Manager.
 * - **FaceTec Keys**: If using biometric enrollment, these keys should be securely managed.
 *
 * ## Configuration Validation
 * The [ScytalesSdkInitializer] validates required configurations during initialization and will
 * fail with appropriate error messages if configurations are missing or invalid.
 *
 * @see ScytalesSdkInitializer for SDK initialization
 * @see isFaceTecConfigured to check if FaceTec is properly configured
 */
object SdkConfig {
    /**
     * Scytales license key (REQUIRED).
     *
     * This key is used to authenticate your application with the Scytales SDK services.
     * The license key is validated during SDK initialization and must be valid for the
     * SDK to function properly.
     *
     * ## Format
     * License keys follow the format: `XXXXX-XXXXX-XXXXX-XXXXX`
     *
     * ## Security Note
     * For production applications, consider using one of the following approaches:
     * - Store in BuildConfig and exclude from version control
     * - Use Android Keystore for secure storage
     * - Retrieve from a secure backend service
     *
     * ## Obtaining a License Key
     * Contact Scytales support or your organization administrator to obtain a valid license key.
     *
     * @see ScytalesSdkInitializer.initialize for license validation during initialization
     */
    const val licenseKey: String = ""

    /**
     * Organization URL for Scytales Manager features (REQUIRED).
     *
     * This URL points to your organization's Scytales Manager API endpoint, which provides
     * features such as:
     * - User signup and enrollment
     * - Credential issuance management
     * - Organization-specific configurations
     * - Biometric enrollment (if configured)
     *
     * ## Format
     * The URL should be a complete HTTPS endpoint including the API version path.
     * Example: `https://api.your-organization.scytales.com/api/v3`
     *
     * ## Network Requirements
     * - Must be accessible from the device
     * - Requires HTTPS (HTTP not supported for security reasons)
     * - Must match the organization configured in your Scytales Manager dashboard
     *
     * @see ScytalesSdkInitializer.initialize for organization validation during initialization
     */
    const val organizationUrl: String = ""

    /**
     * OIDC Redirect URI for signup flow with OpenID Connect (REQUIRED).
     *
     * This URI is used during the OpenID Connect authentication flow to redirect the user
     * back to the application after successful authentication with the identity provider.
     * It follows the custom URL scheme pattern to enable deep linking.
     *
     * ## Configuration Synchronization
     * This value must be configured in three places:
     * 1. **Here in SdkConfig** - For SDK initialization
     * 2. **build.gradle.kts** - In manifestPlaceholders for AndroidManifest generation
     * 3. **Scytales Manager** - In the OIDC client configuration
     *
     * ## Gradle Configuration Example
     * ```kotlin
     * android {
     *     defaultConfig {
     *         // ... other configurations
     *
     *         manifestPlaceholders["appAuthRedirectScheme"] = "com.scytales"
     *         manifestPlaceholders["appAuthRedirectHost"] = "wallet"
     *         manifestPlaceholders["appAuthRedirectPath"] = "oidc"
     *
     *         // ... other configurations
     *     }
     * }
     * ```
     *
     * ## URI Format
     * The URI follows the pattern: `scheme://host/path`
     * - **Scheme**: Custom URL scheme (e.g., `com.scytales`)
     * - **Host**: App-specific host (e.g., `wallet`)
     * - **Path**: Redirect path (e.g., `oidc`)
     *
     * ## Deep Link Handling
     * Ensure your MainActivity or appropriate activity handles this intent filter
     * to receive the authentication callback.
     *
     * @see ScytalesSdkInitializer.initialize for OIDC configuration during initialization
     */
    const val singupOidcRedirectUri: String = "com.scytales://wallet/oidc"
}

