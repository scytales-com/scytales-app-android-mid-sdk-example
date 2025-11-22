//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.manager](../../index.md)/[ScytalesManager](../index.md)/[Builder](index.md)

# Builder

interface [Builder](index.md)

Builder interface for constructing [ScytalesManager](../index.md) instances.

This builder provides a fluent API for configuring the Scytales Manager with custom settings for wallet instance provisioning, document creation, and network operations.

Example usage:

```kotlin
val manager = ScytalesManager.Builder()
    .walletInstanceProvider(customProvider)
    .createDocumentSettings(customSettings)
    .ktorHttpClient(customHttpClient)
    .build()
```

#### See also

| |
|---|
| ScytalesManagerBuilder |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>abstract suspend fun [build](build.md)(): [ScytalesManager](../index.md)<br>Builds and returns a configured [ScytalesManager](../index.md) instance. |
| [createDocumentSettings](create-document-settings.md) | [Scytales MID SDK]<br>abstract fun [createDocumentSettings](create-document-settings.md)(settings: &lt;Error class: unknown class&gt;): [ScytalesManager.Builder](index.md)<br>Sets the document creation settings. |
| [ktorHttpClient](ktor-http-client.md) | [Scytales MID SDK]<br>abstract fun [ktorHttpClient](ktor-http-client.md)(ktorHttpClient: &lt;Error class: unknown class&gt;): [ScytalesManager.Builder](index.md)<br>Sets a custom Ktor HTTP client for network operations. |
| [walletInstanceProvider](wallet-instance-provider.md) | [Scytales MID SDK]<br>abstract fun [walletInstanceProvider](wallet-instance-provider.md)(provider: [WalletInstanceProvider](../../../com.scytales.mid.sdk.manager.wallet/-wallet-instance-provider/index.md)): [ScytalesManager.Builder](index.md)<br>Sets the wallet instance provider. |
