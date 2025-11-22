# Initialization

Initialize the Scytales MID SDK in your Android application.

## Overview

The SDK must be initialized before any features can be used. Initialization configures:

- License validation and activation
- Secure document storage
- Cryptographic key management
- OpenID4VCI credential issuance
- Proximity presentation (BLE)
- Remote presentation (OpenID4VP)
- Digital Credentials API (DCAPI)
- Scytales Manager integration

## Initialization Architecture

The example application uses a **singleton initializer pattern** for thread-safe SDK initialization:

- **`ScytalesSdkInitializer`**: Singleton object managing SDK lifecycle
- **`SdkConfig`**: Configuration object with license and settings
- **`SdkState`**: Sealed class representing initialization state

## Basic Initialization

### Step 1: Create SDK Initializer

Create a singleton initializer object:

**File**: `app/src/main/java/com/scytales/mid/sdk/example/app/sdk/ScytalesSdkInitializer.kt`

```kotlin
object ScytalesSdkInitializer {
    @Volatile
    private var sdkInstance: Sdk? = null

    private val initializationMutex = Mutex()

    suspend fun initialize(context: Context): Result<Sdk> = initializationMutex.withLock {
        sdkInstance?.let { return@withLock Result.success(it) }

        try {
            val sdk = Sdk(context.applicationContext) {
                license(LicenseConfig.key(
                    licenseKey = SdkConfig.licenseKey,
                    cacheLocation = context.cacheDir.absolutePath
                ))

                configure {
                    wallet {
                        configureDocumentManager(storageFile.absolutePath)
                        configureOpenId4Vci { /* ... */ }
                        configureProximityPresentation(/* ... */)
                        configureOpenId4Vp { /* ... */ }
                        configureDCAPI { /* ... */ }
                    }

                    manager {
                        organization = Organization.url(SdkConfig.organizationUrl)
                    }
                }
            }

            sdkInstance = sdk
            Result.success(sdk)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSdk(): Sdk = sdkInstance ?: error("SDK not initialized")
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/ScytalesSdkInitializer.kt)

### Step 2: Initialize in Application Class

Initialize the SDK in your `Application` class:

**File**: `app/src/main/java/com/scytales/mid/sdk/example/app/ScytalesApp.kt`

```kotlin
class ScytalesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        runBlocking {
            ScytalesSdkInitializer.initialize(this@ScytalesApp)
                .onSuccess {
                    Log.d(TAG, "SDK initialized successfully")
                }
                .onFailure { error ->
                    Log.e(TAG, "SDK initialization failed", error)
                }
        }
    }
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ScytalesApp.kt)

### Step 3: Register Application Class

Register your `Application` class in `AndroidManifest.xml`:

```xml
<application
    android:name=".ScytalesApp"
    android:theme="@style/YourTheme"
    tools:replace="android:theme">
    <!-- ... -->
</application>
```

## SDK Configuration

### License Configuration

The license must be configured first:

```kotlin
license(LicenseConfig.key(
    licenseKey = SdkConfig.licenseKey,
    cacheLocation = context.cacheDir.absolutePath
))
```

**Parameters:**
- `licenseKey`: Your Scytales license key (mandatory)
- `cacheLocation`: Cache directory for license validation (optional, improves performance)

### Wallet Configuration

Configure core wallet features:

```kotlin
configure {
    wallet {
        // Document storage
        configureDocumentManager(storageFile.absolutePath)
        
        // Logging
        configureLogging(level = Logger.LEVEL_DEBUG)
        
        // Key creation
        configureDocumentKeyCreation(
            userAuthenticationRequired = false,
            userAuthenticationTimeout = Duration.INFINITE,
            useStrongBoxForKeys = true
        )
    }
}
```

#### Document Manager

Configure secure document storage location:

```kotlin
val storageFile = File(context.noBackupFilesDir, "scytales_wallet.db")
configureDocumentManager(storageFile.absolutePath)
```

**Storage Location:**
- Uses `noBackupFilesDir` to exclude from cloud backups
- Encrypted with Android Keystore
- Persists between app restarts

#### Logging

Configure SDK logging level:

```kotlin
configureLogging(level = Logger.LEVEL_DEBUG)
```

**Levels:**
- `LEVEL_VERBOSE`: All logs (development)
- `LEVEL_DEBUG`: Debug and higher
- `LEVEL_INFO`: Info and higher (recommended)
- `LEVEL_WARN`: Warnings and errors only
- `LEVEL_ERROR`: Errors only
- `LEVEL_NONE`: No logs (production)

#### Document Key Creation

Configure cryptographic key management:

```kotlin
configureDocumentKeyCreation(
    userAuthenticationRequired = false,
    userAuthenticationTimeout = Duration.INFINITE,
    useStrongBoxForKeys = true
)
```

**Parameters:**
- `userAuthenticationRequired`: Require biometric/PIN for key usage
- `userAuthenticationTimeout`: Validity duration after authentication
- `useStrongBoxForKeys`: Use hardware-backed keys (StrongBox) when available

### OpenID4VCI Configuration

Configure OpenID for Verifiable Credential Issuance:

```kotlin
configureOpenId4Vci {
    withIssuerUrl("https://issuer.example.com")
    withClientId("wallet-client")
    withAuthFlowRedirectionURI("eudi-openid4ci://authorize")
}
```

**Parameters:**
- `withIssuerUrl`: Default issuer URL (can be overridden per credential offer)
- `withClientId`: OAuth2 client identifier
- `withAuthFlowRedirectionURI`: Deep link URI for authorization redirect

**Deep Link Setup:**

Add intent filter to `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    
    <data
        android:scheme="eudi-openid4ci"
        android:host="authorize" />
</intent-filter>
```

### Proximity Presentation Configuration

Configure Bluetooth Low Energy proximity presentation:

```kotlin
configureProximityPresentation(
    enableBlePeripheralMode = true,
    enableBleCentralMode = true,
    clearBleCache = true,
    nfcEngagementServiceClass = null
)
```

**Parameters:**
- `enableBlePeripheralMode`: Act as holder (wallet) for verifier connections
- `enableBleCentralMode`: Connect to verifiers as central device
- `clearBleCache`: Clear BLE cache on initialization
- `nfcEngagementServiceClass`: NFC engagement service (optional, for tap-to-share)

**Permissions Required:**

Add to `AndroidManifest.xml`:

```xml
<!-- Bluetooth permissions for proximity presentation -->

<!-- Android 11 and below (deprecated in Android 12+) -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />

<!-- Android 12+ (API 31+) -->
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" />

<!-- Location permission (required for BLE scanning on Android 11 and below) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

**Permission Notes:**
- `maxSdkVersion="30"`: Old Bluetooth permissions only apply up to Android 11
- `usesPermissionFlags="neverForLocation"`: Declares BLUETOOTH_SCAN is not used for location tracking
- `ACCESS_FINE_LOCATION`: Required for BLE scanning on Android 6-11; runtime permission needed

### Remote Presentation Configuration

Configure OpenID for Verifiable Presentations:

```kotlin
configureOpenId4Vp {
    withSchemes("mdoc-openid4vp")
    withFormats(Format.SdJwtVc.ES256, Format.MsoMdoc.ES256)
    withClientIdSchemes(
        ClientIdScheme.RedirectUri,
        ClientIdScheme.X509Hash,
        ClientIdScheme.X509SanDns
    )
}
```

**Parameters:**
- `withSchemes`: Supported URI schemes for presentation requests
- `withFormats`: Supported credential formats (SD-JWT-VC, mso_mdoc)
- `withClientIdSchemes`: Supported verifier authentication schemes

**Deep Link Setup:**

Add intent filter to `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    
    <data android:scheme="mdoc-openid4vp" android:host="*" />
</intent-filter>
```

### DCAPI Configuration

Configure Digital Credentials API for browser integration.

**Protocol**: DCAPI uses the `org-iso-mdoc` protocol according to [ISO/IEC TS 18013-7:2025](https://www.iso.org/standard/91154.html) Annex C.

**Important**: DCAPI is **disabled by default** and must be explicitly enabled.

```kotlin
configureDCAPI {
    withEnabled(true) // Explicitly enable DCAPI
}
```

**Parameters:**
- `withEnabled`: Enable/disable DCAPI support (default: `false`)

**Intent Filter Setup:**

Add to `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="androidx.credentials.registry.provider.action.GET_CREDENTIAL" />
    <action android:name="androidx.identitycredentials.action.GET_CREDENTIALS" />
    <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

### Scytales Manager Configuration

Configure Scytales-specific features:

```kotlin
manager {
    // Organization URL (mandatory)
    organization = Organization.url(SdkConfig.organizationUrl)
    
    signup {
        // FaceTec biometric enrollment (optional)
        if (SdkConfig.isFaceTecConfigured) {
            facetec {
                deviceKeyIdentifier(SdkConfig.faceTecDeviceKey!!)
                publicFaceScanEncryptionKey(SdkConfig.faceTecPublicKey!!)
            }
        }
        
        // OpenID Connect configuration (mandatory for signup flow)
        openIdConnect {
            redirectUri = SdkConfig.singupOidcRedirectUri.toUri()
        }
    }
}
```

**Organization Configuration:**
- Mandatory for all Scytales Manager features
- Required for `getAvailableDocumentTypes()` and `createSignManager()`
- Uses organization base URL from `SdkConfig.organizationUrl`

**FaceTec Configuration:**
- Optional biometric enrollment during document issuance
- Both device key and public key required
- Obtained from Scytales and FaceTec

**OpenID Connect Configuration:**
- **Mandatory** for the signup flow with OpenID Connect
- `redirectUri`: Deep link URI for OAuth callback (from `SdkConfig.singupOidcRedirectUri`)
- Must match the URI configured in `build.gradle.kts` manifestPlaceholders
- Must be registered in your Scytales Manager OIDC client settings
- Requires `androidx.core.net.toUri()` extension to convert String to Uri

### AndroidManifest Configuration for OIDC Redirect

The OpenID Connect signup flow requires a redirect handler activity in `AndroidManifest.xml`:

**File**: `app/src/main/AndroidManifest.xml`

```xml
<application>
    <!-- Your main activity -->
    <activity android:name=".MainActivity">
        <!-- ... -->
    </activity>
    
    <!-- OpenID Connect redirect handler for signup flow -->
    <activity
        android:name="net.openid.appauth.RedirectUriReceiverActivity"
        android:exported="true"
        tools:node="replace">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />

            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />

            <data android:scheme="${appAuthRedirectScheme}" />
            <data android:host="${appAuthRedirectHost}" />
            <data android:pathPrefix="${appAuthRedirectPath}" />
        </intent-filter>
    </activity>
</application>
```

**How it works:**

- `RedirectUriReceiverActivity` is provided by the AppAuth library
  (dependency of Scytales MID SDK)
- The activity intercepts OAuth callbacks during OpenID Connect signup
- Uses `manifestPlaceholders` from `build.gradle.kts` to construct the
  redirect URI
- `tools:node="replace"` ensures this configuration overrides any library
  defaults
- Must be `android:exported="true"` to receive external intents from
  browser/auth provider

**Placeholder Resolution:**

With the default `build.gradle.kts` configuration:

```kotlin
manifestPlaceholders["appAuthRedirectScheme"] = "com.scytales"
manifestPlaceholders["appAuthRedirectHost"] = "wallet"
manifestPlaceholders["appAuthRedirectPath"] = "/oidc"
```

The intent filter resolves to intercept: `com.scytales://wallet/oidc`

**Troubleshooting:**

- If signup redirect fails, verify the URI matches in all three places:
  1. `build.gradle.kts` manifestPlaceholders
  2. `SdkConfig.singupOidcRedirectUri`
  3. Scytales Manager OIDC client configuration

## Accessing the SDK

### Get SDK Instance

Access the initialized SDK from anywhere in your application:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()
```

**Throws** `IllegalStateException` if SDK is not initialized.

### Check Initialization State

Check if SDK is initialized:

```kotlin
if (ScytalesSdkInitializer.isInitialized()) {
    val sdk = ScytalesSdkInitializer.getSdk()
}
```

### Get Initialization State

Get detailed initialization state:

```kotlin
when (val state = ScytalesSdkInitializer.getState()) {
    SdkState.NotInitialized -> Log.d(TAG, "SDK not initialized")
    SdkState.Initializing -> Log.d(TAG, "SDK initializing...")
    is SdkState.Initialized -> Log.d(TAG, "SDK ready")
    is SdkState.Failed -> Log.e(TAG, "SDK failed", state.error)
}
```

## Error Handling

### Initialization Errors

Handle initialization errors gracefully:

```kotlin
ScytalesSdkInitializer.initialize(context)
    .onSuccess { sdk ->
        Log.d(TAG, "SDK initialized successfully")
    }
    .onFailure { error ->
        when (error) {
            is SdkInitializationError.InvalidLicense -> {
                Log.e(TAG, "Invalid license key", error)
                // Show error to user
            }
            else -> {
                Log.e(TAG, "SDK initialization failed", error)
                // Show generic error
            }
        }
    }
```

## Best Practices

### 1. Initialize Early

Initialize SDK in `Application.onCreate()` to ensure it's ready before any features are used.

### 2. Handle Initialization Asynchronously

Use coroutines for non-blocking initialization:

```kotlin
lifecycleScope.launch {
    ScytalesSdkInitializer.initialize(context)
        .onSuccess { /* Navigate to home */ }
        .onFailure { /* Show error */ }
}
```

### 3. Show Loading State

Display loading UI while SDK initializes:

```kotlin
@Composable
fun SplashScreen() {
    val state by viewModel.state.collectAsState()
    
    when (state) {
        SdkState.Initializing -> LoadingIndicator()
        is SdkState.Initialized -> LaunchedEffect(Unit) { /* Navigate */ }
        is SdkState.Failed -> ErrorScreen()
    }
}
```

### 4. Validate Configuration

Validate configuration before initialization:

```kotlin
if (SdkConfig.licenseKey.isBlank()) {
    error("License key not configured")
}
```

### 5. Use Thread-Safe Access

The `ScytalesSdkInitializer` singleton is thread-safe. Multiple concurrent
initialization calls are safe.

## Next Steps

- [**Document Issuance**](../features/document-issuance.md) -
  Issue digital credentials
- [**Document Presentation**](../features/document-presentation.md) -
  Present credentials to verifiers
- [**Document Management**](../features/document-management.md) -
  Manage stored documents

## Reference

- [**ScytalesSdkInitializer.kt**][init-kt] - Full initializer implementation
- [**ScytalesApp.kt**][app-kt] - Application class example
- [**SdkState.kt**][state-kt] - State representation
- [**SDK API Documentation**](../api/index.md) - Complete SDK reference

[init-kt]: ../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/ScytalesSdkInitializer.kt
[app-kt]: ../../app/src/main/java/com/scytales/mid/sdk/example/app/ScytalesApp.kt
[state-kt]: ../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/SdkState.kt

