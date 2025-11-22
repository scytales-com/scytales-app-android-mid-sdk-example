//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[SdkConfig](../index.md)/[Builder](index.md)

# Builder

interface [Builder](index.md)

Builder interface for constructing [SdkConfig](../index.md) instances.

This interface defines the contract for building SDK configurations with a fluent API that supports both DSL-style configuration blocks and direct configuration object assignment.

#### Inheritors

| |
|---|
| [SdkConfigBuilder](../../-sdk-config-builder/index.md) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>abstract suspend fun [build](build.md)(): [SdkConfig](../index.md)<br>Build the final [SdkConfig](../index.md) instance. |
| [manager](manager.md) | [Scytales MID SDK]<br>abstract fun [manager](manager.md)(config: [ScytalesManagerConfig](../../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)): [SdkConfig.Builder](index.md)<br>Set Scytales manager configuration directly.<br>[Scytales MID SDK]<br>abstract fun [manager](manager.md)(block: [ScytalesManagerConfigBuilder](../../../com.scytales.mid.sdk.manager/-scytales-manager-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SdkConfig.Builder](index.md)<br>Configure Scytales manager settings using a DSL block. |
| [wallet](wallet.md) | [Scytales MID SDK]<br>abstract fun [wallet](wallet.md)(config: &lt;Error class: unknown class&gt;): [SdkConfig.Builder](index.md)<br>Set wallet configuration directly.<br>[Scytales MID SDK]<br>abstract fun [wallet](wallet.md)(block: &lt;Error class: unknown class&gt;.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SdkConfig.Builder](index.md)<br>Configure wallet settings using a DSL block. |
