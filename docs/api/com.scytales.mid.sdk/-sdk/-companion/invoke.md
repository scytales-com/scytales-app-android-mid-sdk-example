//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[Sdk](../index.md)/[Companion](index.md)/[invoke](invoke.md)

# invoke

[Scytales MID SDK]\
suspend operator fun [invoke](invoke.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), builder: [SdkBuilder](../../-sdk-builder/index.md).(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk](../index.md)

Creates a new SDK instance using a builder DSL.

This is the recommended way to create SDK instances. It provides a fluent DSL that guides you through the configuration process and ensures all required settings are provided.

The builder pattern allows for flexible configuration with sensible defaults while maintaining the ability to customize both the EUDI Wallet and Scytales manager components independently.

#### Return

Fully configured and initialized [Sdk](../index.md) instance

Example usage:

```kotlin
val sdk = Sdk(context) {
    configure {
        wallet {
            configureDocumentManager(storageFile.absolutePath)
            configureLogging(level = Logger.LEVEL_INFO)
            configureOpenId4Vci {
                withIssuerUrl("https://issuer.example.com")
                withClientId("client-id")
            }
        }
        manager {
            organization = Organization.url("https://your-scytales-manager.url")
            signup {
                facetec {
                    deviceKeyIdentifier("your-device-key")
                    publicFaceScanEncryptionKey("your-encryption-key")
                }
                openIdConnect {
                    redirectUri = Uri.parse("your-app://callback")
                }
            }
        }
    }
    // Optional: customize wallet-specific settings
    customizeWallet {
        withLogger(customLogger)
    }
    // Optional: customize manager-specific settings
    customizeManager {
        createDocumentSettings(customSettings)
    }
}
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| context | Android application context required for SDK initialization |
| builder | Configuration lambda with [SdkBuilder](../../-sdk-builder/index.md) as receiver and context parameter |

#### See also

| | |
|---|---|
| [SdkBuilder](../../-sdk-builder/index.md) | for available configuration methods |
| [SdkConfig](../../-sdk-config/index.md) | for configuration options |
