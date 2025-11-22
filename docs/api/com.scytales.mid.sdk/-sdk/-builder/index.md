//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[Sdk](../index.md)/[Builder](index.md)

# Builder

interface [Builder](index.md)

Builder interface for constructing [Sdk](../index.md) instances.

Provides a fluent API with three levels of configuration:

1. 
   **High-level configuration** via [configure](configure.md) - Sets up both wallet and manager using [SdkConfig](../../-sdk-config/index.md)
2. 
   **Wallet customization** via [customizeWallet](customize-wallet.md) - Fine-tunes EUDI Wallet components
3. 
   **Manager customization** via [customizeManager](customize-manager.md) - Fine-tunes Scytales manager components

The builder ensures that all required configurations are provided and validates settings before constructing the final SDK instance.

#### See also

| | |
|---|---|
| [Sdk](../index.md) | for the resulting SDK interface |
| [SdkConfig](../../-sdk-config/index.md) | for configuration options |

#### Inheritors

| |
|---|
| [SdkBuilder](../../-sdk-builder/index.md) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>abstract suspend fun [build](build.md)(): [Sdk](../index.md)<br>Build and initialize the SDK instance. |
| [configure](configure.md) | [Scytales MID SDK]<br>abstract fun [configure](configure.md)(configure: [SdkConfig.Builder](../../-sdk-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](index.md)<br>Configure the SDK using a unified configuration DSL. |
| [customizeManager](customize-manager.md) | [Scytales MID SDK]<br>abstract fun [customizeManager](customize-manager.md)(customize: [ScytalesManager.Builder](../../../com.scytales.mid.sdk.manager/-scytales-manager/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](index.md)<br>Customize the Scytales manager with advanced settings. |
| [customizeWallet](customize-wallet.md) | [Scytales MID SDK]<br>abstract fun [customizeWallet](customize-wallet.md)(customize: &lt;Error class: unknown class&gt;.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](index.md)<br>Customize the Wallet with advanced component overrides. |
