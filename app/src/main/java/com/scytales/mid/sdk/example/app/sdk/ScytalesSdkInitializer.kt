package com.scytales.mid.sdk.example.app.sdk

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.scytales.mid.sdk.Sdk
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer.getSdk
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer.getState
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer.initialize
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer.isInitialized
import com.scytales.mid.sdk.example.app.sdk.error.SdkInitializationError
import com.scytales.mid.sdk.license.LicenseConfig
import com.scytales.mid.sdk.manager.Organization
import eu.europa.ec.eudi.wallet.logging.Logger
import eu.europa.ec.eudi.wallet.transfer.openId4vp.ClientIdScheme
import eu.europa.ec.eudi.wallet.transfer.openId4vp.Format
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.time.Duration

/**
 * Singleton initializer for Scytales MID SDK.
 *
 * This object handles the initialization and lifecycle management of the Scytales MID SDK,
 * providing thread-safe access to the SDK instance throughout the application. It ensures
 * that the SDK is initialized only once and provides a centralized point of access.
 *
 * ## Features
 * - Thread-safe initialization using Mutex
 * - Single instance guarantee (singleton pattern)
 * - State tracking and management
 * - Comprehensive error handling and mapping
 * - Configuration validation
 *
 * ## Configuration
 * The SDK requires configuration through [SdkConfig] before initialization:
 * - License key (required)
 * - Organization URL (required)
 * - FaceTec configuration (optional)
 *
 * ## Usage Example
 * ```kotlin
 * // 1. Configure the SDK
 * SdkConfig.licenseKey = "your-license-key"
 * SdkConfig.organizationUrl = "https://your-organization.scytales.com"
 *
 * // 2. Initialize the SDK (suspend function, call from coroutine)
 * val result = ScytalesSdkInitializer.initialize(context)
 * result.fold(
 *     onSuccess = { sdk ->
 *         Log.d(TAG, "SDK initialized successfully")
 *     },
 *     onFailure = { error ->
 *         Log.e(TAG, "SDK initialization failed", error)
 *     }
 * )
 *
 * // 3. Access the SDK instance anywhere in your app
 * val sdk = ScytalesSdkInitializer.getSdk()
 * ```
 *
 * ## Initialization Details
 * The initialization process includes:
 * - License validation and configuration
 * - Wallet setup with document manager
 * - OpenID4VCI configuration for credential issuance
 * - Proximity presentation (BLE and NFC)
 * - OpenID4VP configuration for credential presentation
 * - Digital Credentials API (DCAPI) setup
 * - Scytales Manager configuration (organization, signup, biometrics)
 *
 * @see SdkConfig for configuration options
 * @see SdkState for initialization state tracking
 * @see SdkInitializationError for possible error types
 */
object ScytalesSdkInitializer {
    private const val TAG = "ScytalesSdkInit"

    @Volatile
    private var sdkInstance: Sdk? = null

    private val initializationMutex = Mutex()

    @Volatile
    private var initializationState: SdkState = SdkState.NotInitialized

    /**
     * Get the current SDK initialization state.
     *
     * This method returns the current state of the SDK initialization process,
     * which can be one of: [SdkState.NotInitialized], [SdkState.Initializing],
     * [SdkState.Initialized], or [SdkState.Failed].
     *
     * @return The current [SdkState]
     *
     * @see SdkState
     */
    fun getState(): SdkState = initializationState

    /**
     * Check if SDK is initialized.
     *
     * This is a convenience method to quickly check if the SDK has been successfully
     * initialized and is ready to use.
     *
     * @return `true` if SDK is initialized, `false` otherwise
     *
     * @see getState for detailed state information
     * @see getSdk to access the SDK instance
     */
    fun isInitialized(): Boolean = sdkInstance != null

    /**
     * Get the initialized SDK instance.
     *
     * This method provides access to the SDK instance after it has been successfully
     * initialized. The instance can be used to access all SDK functionality including
     * wallet operations, document management, and credential issuance/presentation.
     *
     * @return The initialized [Sdk] instance
     * @throws IllegalStateException if SDK is not initialized. Call [initialize] first.
     *
     * @see initialize to initialize the SDK
     * @see isInitialized to check initialization status
     */
    fun getSdk(): Sdk {
        return sdkInstance ?: throw IllegalStateException(
            "SDK not initialized. Call ScytalesSdkInitializer.initialize(context) first."
        )
    }

    /**
     * Initialize the SDK with configuration from [SdkConfig] object.
     *
     * This method performs a complete SDK initialization with the following steps:
     * 1. Validates required configuration (license key and organization URL)
     * 2. Configures license and cache location
     * 3. Sets up wallet with document storage and key management
     * 4. Configures OpenID4VCI for credential issuance
     * 5. Enables proximity presentation (BLE peripheral/central modes)
     * 6. Configures OpenID4VP for credential presentation
     * 7. Enables Digital Credentials API (DCAPI)
     * 8. Configures Scytales Manager with organization and signup options
     * 9. Sets up FaceTec biometric enrollment (if configured)
     *
     * ## Thread Safety
     * This method is thread-safe and uses a mutex to ensure only one initialization
     * occurs at a time. Subsequent calls will return the existing instance without
     * re-initializing.
     *
     * ## Configuration Requirements
     * Before calling this method, you must configure:
     * - [SdkConfig.licenseKey] - Your Scytales license key (required)
     * - [SdkConfig.organizationUrl] - Your organization URL (required)
     * - [SdkConfig.singupOidcRedirectUri] - Redirect URI for OIDC signup (required)
     *
     * ## Example Usage
     * ```kotlin
     * // Configure SDK before initialization
     * SdkConfig.licenseKey = "your-license-key"
     * SdkConfig.organizationUrl = "https://your-organization.scytales.com"
     *
     * // Initialize from a coroutine
     * lifecycleScope.launch {
     *     val result = ScytalesSdkInitializer.initialize(applicationContext)
     *     result.fold(
     *         onSuccess = { sdk ->
     *             Log.d(TAG, "SDK ready to use")
     *             // Access wallet, manager, etc.
     *         },
     *         onFailure = { error ->
     *             Log.e(TAG, "Initialization failed", error)
     *             // Handle error (e.g., show error dialog)
     *         }
     *     )
     * }
     * ```
     *
     * @param context Application context (will be converted to applicationContext internally)
     * @return [Result] containing the initialized [Sdk] instance on success, or a
     *         [SdkInitializationError] on failure
     *
     * @see SdkConfig for configuration options
     * @see SdkInitializationError for possible error types
     * @see getSdk to access the SDK after initialization
     */
    suspend fun initialize(context: Context): Result<Sdk> = initializationMutex.withLock {
        // Return existing instance if already initialized
        sdkInstance?.let {
            Log.d(TAG, "SDK already initialized, returning existing instance")
            return@withLock Result.success(it)
        }

        initializationState = SdkState.Initializing
        Log.i(TAG, "Starting SDK initialization...")

        // Validate configuration
        if (SdkConfig.licenseKey.isBlank()) {
            val error = SdkInitializationError.InvalidLicense(
                "License key not configured. Add it to SdkConfig.licenseKey in the code."
            )
            initializationState = SdkState.Failed(error)
            return@withLock Result.failure(error)
        }

        if (SdkConfig.organizationUrl.isBlank()) {
            val error = SdkInitializationError.InvalidOrganization(
                "Organization URL not configured. Add it to SdkConfig.organizationUrl in the code."
            )
            initializationState = SdkState.Failed(error)
            return@withLock Result.failure(error)
        }

        Log.d(TAG, "License key: configured")
        Log.d(TAG, "Organization: ${SdkConfig.organizationUrl}")

        try {
            // Prepare storage location
            val storageFile = File(context.noBackupFilesDir, "scytales_wallet.db")
            val cacheLocation = context.cacheDir.absolutePath
            val logLevel = Logger.LEVEL_DEBUG

            Log.d(TAG, "Storage: ${storageFile.absolutePath}")

            // Initialize SDK
            val sdk = Sdk(context.applicationContext) {

                // 1. Configure license (REQUIRED)
                Log.d(TAG, "Configuring license...")
                license(
                    LicenseConfig.key(
                        licenseKey = SdkConfig.licenseKey,
                        cacheLocation = cacheLocation
                    )
                )

                // 2. Configure wallet and manager
                configure {
                    // Wallet configuration
                    wallet {
                        Log.d(TAG, "Configuring wallet...")

                        configureDocumentManager(storageFile.absolutePath)
                        configureLogging(level = logLevel)
                        configureDocumentKeyCreation(
                            userAuthenticationRequired = false,
                            userAuthenticationTimeout = Duration.INFINITE,
                            useStrongBoxForKeys = true
                        )
                        configureOpenId4Vci {
                            withIssuerUrl("https://dev.issuer-backend.eudiw.dev")
                            withClientId("wallet-dev")
                            withAuthFlowRedirectionURI("eudi-openid4ci://authorize")
                        }

                        // Configure proximity presentation (BLE + QR)
                        Log.d(TAG, "Configuring proximity presentation...")
                        configureProximityPresentation(
                            // Enable BLE peripheral mode (wallet acting as holder)
                            enableBlePeripheralMode = true,
                            // Enable BLE central mode (for connecting to verifiers)
                            enableBleCentralMode = true,
                            // Clear BLE cache on start
                            clearBleCache = true,
                            // NFC engagement service (not configured for now)
                            nfcEngagementServiceClass = null
                        )
                        configureOpenId4Vp {
                            withSchemes("mdoc-openid4vp")
                            withFormats(Format.SdJwtVc.ES256, Format.MsoMdoc.ES256)
                            withClientIdSchemes(
                                ClientIdScheme.RedirectUri,
                                ClientIdScheme.X509Hash,
                                ClientIdScheme.X509SanDns
                            )
                        }

                        // Configure DCAPI (Digital Credentials API)
                        Log.d(TAG, "Enabling DCAPI (Digital Credentials API)...")
                        configureDCAPI {
                            withEnabled(true)
                        }
                    }

                    // Manager configuration (optional - only if organization is configured)

                    manager {
                        Log.d(TAG, "Configuring Scytales manager...")
                        organization = Organization.url(SdkConfig.organizationUrl)
                        signup {

                            openIdConnect {
                                redirectUri = SdkConfig.singupOidcRedirectUri.toUri()
                            }
                        }
                    }
                }
                customizeWallet {
                    withLogger { record ->
                        record.thrown?.let {
                            Log.d(record.sourceClassName, record.message, it)
                        } ?: run {
                            Log.d(record.sourceClassName, record.message)
                        }

                    }
                }
            }

            sdkInstance = sdk
            initializationState = SdkState.Initialized(sdk)

            Log.i(TAG, "SDK initialized successfully!")
            Result.success(sdk)

        } catch (e: Exception) {
            initializationState = SdkState.Failed(e)

            Log.e(TAG, "SDK initialization failed", e)
            Result.failure(e)
        }
    }

    /**
     * Reset SDK instance (useful for testing).
     *
     * This method destroys the current SDK instance and resets the initialization state
     * to [SdkState.NotInitialized]. This allows the SDK to be re-initialized with
     * different configuration.
     *
     * **Warning**: This will destroy the current SDK instance and all its state,
     * including wallet data, documents, and any ongoing operations. Use with caution
     * and only in testing scenarios or when you need to completely restart the SDK.
     *
     * ## Thread Safety
     * This method is thread-safe and uses the same mutex as [initialize] to prevent
     * concurrent access issues.
     *
     * ## Example Usage
     * ```kotlin
     * // In test code
     * @After
     * fun tearDown() = runBlocking {
     *     ScytalesSdkInitializer.reset()
     * }
     * ```
     *
     * @return [Result] with [Unit] on success
     *
     * @see initialize to re-initialize the SDK after reset
     */
    suspend fun reset(): Result<Unit> = initializationMutex.withLock {
        Log.w(TAG, "Resetting SDK instance")
        sdkInstance = null
        initializationState = SdkState.NotInitialized
        Result.success(Unit)
    }
}

