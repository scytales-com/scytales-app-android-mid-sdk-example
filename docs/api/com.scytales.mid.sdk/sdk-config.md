//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk](index.md)/[sdkConfig](sdk-config.md)

# sdkConfig

[Scytales MID SDK]\
suspend fun [sdkConfig](sdk-config.md)(block: [SdkConfig.Builder](-sdk-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SdkConfig](-sdk-config/index.md)

Creates a new [SdkConfig](-sdk-config/index.md) using a DSL builder.

This is the recommended entry point for building SDK configurations. It provides a fluent DSL that allows you to configure both the Wallet functionality and proprietary Scytales features in a single, unified configuration block.

The function creates a [SdkConfigBuilder](-sdk-config-builder/index.md) instance, applies the provided configuration block, and returns the built [SdkConfig](-sdk-config/index.md) instance after validation.

#### Return

Fully configured and validated [SdkConfig](-sdk-config/index.md) instance

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | Configuration lambda with [SdkConfig.Builder](-sdk-config/-builder/index.md) as receiver |

#### See also

| | |
|---|---|
| [SdkConfig](-sdk-config/index.md) | for the resulting configuration object |
| [SdkConfigBuilder](-sdk-config-builder/index.md) | for implementation details |

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if required configurations are missing (e.g., manager configuration)<br>Example usage:<br>```kotlin val config = sdkConfig {     wallet {         configureDocumentManager(storageFile.absolutePath)         configureLogging(level = Logger.LEVEL_INFO)         configureOpenId4Vci {             withIssuerUrl("https://issuer.example.com")             withClientId("client-id")         }     }     manager {         organization = Organization.id("your-organization-id")         signup {             facetec {                 deviceKeyIdentifier("your-device-key")                 publicFaceScanEncryptionKey("your-encryption-key")             }             openIdConnect {                 redirectUri = Uri.parse("your-app://callback")             }         }     } } ``` |
