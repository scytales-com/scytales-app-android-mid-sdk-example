# SDK Initialization

Bring the Scytales MID SDK up once at app start and share the instance for the rest of
the process lifetime.

`Sdk(context) { … }` is a **suspend** builder: it validates the license and prepares
storage, so it must be called from a coroutine, not from `Application.onCreate()`
directly.

## Minimal initialization

```kotlin
import com.scytales.mid.sdk.Sdk
import com.scytales.mid.sdk.license.LicenseConfig
import com.scytales.mid.sdk.manager.Organization
import eu.europa.ec.eudi.wallet.logging.Logger
import java.io.File

suspend fun createSdk(context: Context): Sdk = Sdk(context.applicationContext) {
    license(
        LicenseConfig.key(
            licenseKey = "YOUR-LICENSE-KEY",
            cacheLocation = context.cacheDir.absolutePath
        )
    )

    configure {
        wallet {
            val storageFile = File(context.noBackupFilesDir, "scytales_wallet.db")
            configureDocumentManager(storageFile.absolutePath)
            configureLogging(level = Logger.LEVEL_INFO)
        }

        manager {
            organizations = listOf(
                Organization("My Organization", "https://your-scytales-manager.url")
            )
        }
    }
}
```

Store the wallet database under `noBackupFilesDir`. Credentials are bound to
Keystore keys that cannot survive a restore, so cloud backup would produce a database
the device can no longer open.

## What each block does

### `license { }` — required

Validates your Scytales license. `cacheLocation` holds the validation result so the SDK
does not need the network on every launch. First-time validation does require
connectivity.

### `wallet { }` — EUDI wallet core

Document storage, logging, key policy, and the issuance/presentation protocols.

```kotlin
wallet {
    configureDocumentManager(storageFile.absolutePath)

    // One logger for the whole SDK, signup flows included.
    configureLogging(level = Logger.LEVEL_INFO)

    configureDocumentKeyCreation(
        userAuthenticationRequired = true,
        userAuthenticationTimeout = 30_000.milliseconds,
        useStrongBoxForKeys = true
    )
}
```

Log levels are `Logger.LEVEL_DEBUG` (development), `LEVEL_INFO` (recommended for
production) and `LEVEL_ERROR`.

`configureDocumentKeyCreation` controls how credential keys are protected:

| Option | Effect |
|--------|--------|
| `userAuthenticationRequired` | Require biometric/PIN before a document can be used |
| `userAuthenticationTimeout` | How long an authentication stays valid |
| `useStrongBoxForKeys` | Use hardware-backed StrongBox where the device has it |

> The example app sets `userAuthenticationRequired = false` with
> `Duration.INFINITE` so the demo flows run without prompts. **Enable authentication for
> production.**

### `manager { }` — Scytales backend

Organizations and the signup flow. The SDK takes a *list*, so one app can serve several
issuers and show which one a document type came from:

```kotlin
manager {
    organizations = listOf(
        Organization("Primary Org", "https://your-scytales-manager.url"),
        Organization("Secondary Org", "https://another-scytales-manager.url")
    )

    signup {
        // Required only when the issuer's flow includes an OIDC step.
        openIdConnect {
            redirectUri = "myapp://callback".toUri()
        }
        // Required only for face/ID scan steps — see facetec.md
        facetec {
            deviceKeyIdentifier = "YOUR-DEVICE-KEY"
            publicFaceScanEncryptionKey = "YOUR-PUBLIC-KEY"
        }
    }
}
```

Omitting a block the backend later asks for stops the wizard on error **W00007**
(`ERROR_STEP_NOT_CONFIGURED`). Both blocks are independently optional; see
[FaceTec Biometric Verification](facetec.md).

Organizations can also be added at runtime, in memory only — persist them yourself if
they must survive a restart. Both calls are `suspend`, so they need a coroutine:

```kotlin
lifecycleScope.launch {
    sdk.addOrganization(Organization("New Org", "https://new-org.url"))

    // Which organization issued a document — useful for reissue.
    val org = sdk.getOrganizationForDocument(document)
}
```

## Protocol configuration

All of these sit inside `wallet { }` and are only needed for the flows you actually use.

**OpenID4VCI issuance** — the issuer URL comes from the credential offer, not from here:

```kotlin
configureOpenId4Vci {
    withClientAuthenticationType(
        OpenId4VciManager.ClientAuthenticationType.None("wallet-dev")
    )
    withAuthFlowRedirectionURI("eudi-openid4ci://authorize")
}
```

**Proximity presentation** (ISO 18013-5 over BLE):

```kotlin
configureProximityPresentation(
    enableBlePeripheralMode = true,
    enableBleCentralMode = true,
    clearBleCache = true,
    nfcEngagementServiceClass = null
)
```

**Remote presentation** (OpenID4VP). Keep the schemes in step with the intent filters in
`AndroidManifest.xml`:

```kotlin
configureOpenId4Vp {
    withSchemes("mdoc-openid4vp", "eudi-openid4vp", "haip-vp", "av")
    withFormats(Format.SdJwtVc.ES256, Format.MsoMdoc.ES256)
    withClientIdSchemes(
        ClientIdScheme.RedirectUri,
        ClientIdScheme.X509Hash,
        ClientIdScheme.X509SanDns
    )
}
```

**Digital Credentials API**. When enabled, the protocol set must not be empty — an empty
set is rejected:

```kotlin
configureDCAPI {
    withEnabled(true)
    withSupportedProtocols(DCAPIProtocol.ISO_MDOC)
}
```

## A single, shared instance

Initialize once and reuse. The example app wraps this in a thread-safe singleton that
holds a `Mutex`, so concurrent callers cannot race into two initializations:

```kotlin
object ScytalesSdkInitializer {
    @Volatile private var sdkInstance: Sdk? = null
    private val initializationMutex = Mutex()

    suspend fun initialize(context: Context): Result<Sdk> = initializationMutex.withLock {
        sdkInstance?.let { return@withLock Result.success(it) }

        try {
            val sdk = Sdk(context.applicationContext) { /* … */ }
            sdkInstance = sdk
            Result.success(sdk)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSdk(): Sdk = sdkInstance
        ?: throw IllegalStateException("SDK not initialized. Call initialize(context) first.")
}
```

Returning `Result<Sdk>` rather than throwing lets the UI render an error state — a bad
license key or an unreachable organization URL is a condition to show, not a crash.

Call it from a coroutine and drive your UI off the outcome:

```kotlin
lifecycleScope.launch {
    ScytalesSdkInitializer.initialize(applicationContext).fold(
        onSuccess = { sdk -> /* SDK ready */ },
        onFailure = { error -> /* show error state */ }
    )
}
```

Always pass `applicationContext`. Handing the SDK an Activity context would leak that
Activity for as long as the SDK lives.

Full implementation:
[`ScytalesSdkInitializer.kt`](../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/ScytalesSdkInitializer.kt).

## Troubleshooting

### Initialization fails immediately

Check the license key first, then whether the device can reach the licensing endpoint on
a cold start. Blank required configuration — license key or organization URL — fails
before any network call.

### `IllegalStateException: SDK not initialized`

Something called `getSdk()` before `initialize()` finished. Gate the UI on the
initialization state rather than assuming the SDK is ready.

### Documents disappear after a device restore

The wallet database was backed up and restored onto Keystore keys that no longer exist.
Store it under `noBackupFilesDir`.

### Redirect never comes back to the app

`redirectUri` has to match in three places: this configuration, the
`manifestPlaceholders` in `app/build.gradle.kts`, and the OIDC client registered in
Scytales Manager.

## Next Steps

- [**Downloading SDK Dependencies**](dependencies.md) - Resolve the SDK before this step
- [**FaceTec Biometric Verification**](facetec.md) - Configure the biometric signup step
- [**Configuration**](../getting-started/configuration.md) - License and credential handling
- [**Document Management**](document-management.md) - Use the initialized SDK
