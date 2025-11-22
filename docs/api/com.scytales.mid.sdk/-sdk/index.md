//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[Sdk](index.md)

# Sdk

interface [Sdk](index.md) : [ScytalesManager](../../com.scytales.mid.sdk.manager/-scytales-manager/index.md)

Main interface for the Scytales MID SDK.

**Wallet Features:**

- 
   Document management (storage, retrieval, deletion)
- 
   OpenID4VCI document issuance
- 
   Proximity presentation via BLE and NFC
- 
   Remote presentation via OpenID4VP
- 
   Document status resolution and revocation checking
- 
   Reader certificate validation

**Scytales Proprietary Features:**

- 
   Biometric enrollment with FaceTec integration
- 
   Custom document issuance workflows
- 
   Organization-specific configuration
- 
   Template-based document creation

The SDK is built using a builder pattern and requires both Wallet configuration and Scytales manager configuration. Use the companion object's invoke operator or the [Builder](-builder/index.md) interface to construct instances.

#### See also

| | |
|---|---|
| [SdkConfig](../-sdk-config/index.md) | for configuration options |
| [Sdk.Builder](-builder/index.md) | for constructing SDK instances<br>Example usage:<br>```kotlin val sdk = Sdk(context) {     configure {         wallet {             configureDocumentManager(storageFile.absolutePath)             configureLogging(level = Logger.LEVEL_INFO)         }         manager {             organization = Organization.url("https://your-scytales-manager.url")             signup {                 facetec { /* ... */}                 openIdConnect { /* ... */}             }         }     } }<br>// Use EUDI Wallet features val documents = sdk.getDocuments()<br>// Use Scytales features val availableDocumentTypes = sdk.getAvailableDocumentTypes() val signupManager = sdk.createSignManager() ``` |

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [Scytales MID SDK]<br>interface [Builder](-builder/index.md)<br>Builder interface for constructing [Sdk](index.md) instances. |
| [Companion](-companion/index.md) | [Scytales MID SDK]<br>object [Companion](-companion/index.md)<br>Companion object providing factory methods for creating SDK instances. |

## Functions

| Name | Summary |
|---|---|
| [createSignManager](../../com.scytales.mid.sdk.manager/-scytales-manager/create-sign-manager.md) | [Scytales MID SDK]<br>abstract suspend fun [createSignManager](../../com.scytales.mid.sdk.manager/-scytales-manager/create-sign-manager.md)(): [SignupManager](../../com.scytales.mid.sdk.manager.signup/-signup-manager/index.md)<br>Creates a signup manager for handling user enrollment flows. |
| [getAvailableDocumentTypes](../../com.scytales.mid.sdk.manager/-scytales-manager/get-available-document-types.md) | [Scytales MID SDK]<br>abstract suspend fun [getAvailableDocumentTypes](../../com.scytales.mid.sdk.manager/-scytales-manager/get-available-document-types.md)(): &lt;Error class: unknown class&gt;&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AvailableDocumentType](../../com.scytales.mid.sdk.manager/-available-document-type/index.md)&gt;&gt;<br>Retrieves a list of available document types that can be issued. |
| [getConfiguration](get-configuration.md) | [Scytales MID SDK]<br>abstract fun [getConfiguration](get-configuration.md)(): [SdkConfig](../-sdk-config/index.md)<br>Returns the complete SDK configuration. |
