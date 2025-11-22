//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkConfig](index.md)

# SdkConfig

data class [SdkConfig](index.md)(val walletConfig: &lt;Error class: unknown class&gt;, val managerConfig: [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md))

Configuration container for the Scytales MID SDK.

This class represents a combined configuration for Wallet Core configuration with proprietary Scytales features. It serves as a unified configuration object that provides both the Wallet functionality and additional Scytales-specific capabilities such as biometric enrollment and license management.

The configuration is built using the builder pattern through [SdkConfigBuilder](../-sdk-config-builder/index.md), which provides a fluent DSL for configuring both components.

#### See also

| | |
|---|---|
| EudiWalletConfig | for details on Wallet configuration options |
| [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md) | for details on proprietary Scytales features |
| [SdkConfigBuilder](../-sdk-config-builder/index.md) | for building instances of this configuration<br>Example usage:<br>```kotlin val config = sdkConfig {     wallet {         configureDocumentManager(storageFile.absolutePath)         configureLogging(level = Logger.LEVEL_INFO)         configureOpenId4Vci {             withIssuerUrl("https://issuer.example.com")             withClientId("client-id")         }     }     manager {         organization = Organization.id("your-organization-id")         // Or use custom URLs:         // organization = Organization.urls(         //     issueUrl = "https://api.example.com/issue",         //     reIssueUrl = "https://api.example.com/reissue",         //     templatesUrl = "https://api.example.com/templates"         // )         signup {             facetec {                 deviceKeyIdentifier("your-device-key")                 publicFaceScanEncryptionKey("your-encryption-key")             }             openIdConnect {                 redirectUri = Uri.parse("your-app://callback")             }         }     } } ``` |

## Constructors

| | |
|---|---|
| [SdkConfig](-sdk-config.md) | [Scytales MID SDK]<br>constructor(walletConfig: &lt;Error class: unknown class&gt;, managerConfig: [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)) |

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [Scytales MID SDK]<br>interface [Builder](-builder/index.md)<br>Builder interface for constructing [SdkConfig](index.md) instances. |

## Properties

| Name | Summary |
|---|---|
| [managerConfig](manager-config.md) | [Scytales MID SDK]<br>val [managerConfig](manager-config.md): [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)<br>Configuration for proprietary Scytales manager features, including organization settings, FaceTec biometric enrollment configuration, and OpenID Connect settings for custom document issuance workflows. |
| [walletConfig](wallet-config.md) | [Scytales MID SDK]<br>val [walletConfig](wallet-config.md): &lt;Error class: unknown class&gt;<br>Configuration for the core Wallet functionality, including document management, OpenID4VCI issuance, proximity and remote presentation, reader trust store, and document status resolution. |
