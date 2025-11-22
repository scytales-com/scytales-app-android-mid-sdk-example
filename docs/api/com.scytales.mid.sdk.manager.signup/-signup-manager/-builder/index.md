//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.manager.signup](../../index.md)/[SignupManager](../index.md)/[Builder](index.md)

# Builder

interface [Builder](index.md)

Builder interface for constructing [SignupManager](../index.md) instances.

This builder provides a fluent API for configuring the signup manager with required dependencies including document management, wallet instance provisioning, and document creation settings.

The builder is typically used internally by the Scytales Manager when creating signup managers for user enrollment flows.

Example usage:

```kotlin
val signupManager = SignupManager.Builder()
    .documentManager(myDocumentManager)
    .walletInstanceProvider(myWalletProvider)
    .createDocumentSettings(mySettings)
    .build()
```

#### See also

| |
|---|
| SignupManagerBuilder |
| [SignupManager](../index.md) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>abstract suspend fun [build](build.md)(): [SignupManager](../index.md)<br>Builds and returns a configured [SignupManager](../index.md) instance. |
| [createDocumentSettings](create-document-settings.md) | [Scytales MID SDK]<br>abstract fun [createDocumentSettings](create-document-settings.md)(createDocumentSettings: &lt;Error class: unknown class&gt;): [SignupManager.Builder](index.md)<br>Sets the document creation settings. |
| [documentManager](document-manager.md) | [Scytales MID SDK]<br>abstract fun [documentManager](document-manager.md)(documentManager: &lt;Error class: unknown class&gt;): [SignupManager.Builder](index.md)<br>Sets the document manager for managing document lifecycle. |
| [walletInstanceProvider](wallet-instance-provider.md) | [Scytales MID SDK]<br>abstract fun [walletInstanceProvider](wallet-instance-provider.md)(provider: [WalletInstanceProvider](../../../com.scytales.mid.sdk.manager.wallet/-wallet-instance-provider/index.md)): [SignupManager.Builder](index.md)<br>Sets the wallet instance provider for device attestation. |
