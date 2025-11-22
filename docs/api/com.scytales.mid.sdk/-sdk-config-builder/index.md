//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkConfigBuilder](index.md)

# SdkConfigBuilder

class [SdkConfigBuilder](index.md) : [SdkConfig.Builder](../-sdk-config/-builder/index.md)

Implementation of [SdkConfig.Builder](../-sdk-config/-builder/index.md) that provides a DSL for constructing SDK configurations.

This builder supports a hybrid configuration approach, allowing you to configure both the Wallet functionality and proprietary Scytales features in a unified, fluent API. The builder maintains separate configuration objects for each component and combines them into a final [SdkConfig](../-sdk-config/index.md) instance.

The builder is typically accessed through the top-level [sdkConfig](../sdk-config.md) function rather than being instantiated directly.

Thread safety: This builder is not thread-safe and should only be used from a single thread or with external synchronization.

#### See also

| | |
|---|---|
| [SdkConfig](../-sdk-config/index.md) | for the resulting configuration object |
| [sdkConfig](../sdk-config.md) | for the recommended way to create configurations |

## Properties

| Name | Summary |
|---|---|
| [managerConfig](manager-config.md) | [Scytales MID SDK]<br>var [managerConfig](manager-config.md): [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)?<br>Configuration for Scytales manager proprietary features. |
| [walletConfig](wallet-config.md) | [Scytales MID SDK]<br>var [walletConfig](wallet-config.md): &lt;Error class: unknown class&gt;<br>Configuration for Wallet functionality. |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>open suspend override fun [build](build.md)(): [SdkConfig](../-sdk-config/index.md)<br>Build and validate the final [SdkConfig](../-sdk-config/index.md) instance. |
| [manager](manager.md) | [Scytales MID SDK]<br>open override fun [manager](manager.md)(config: [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)): &lt;Error class: unknown class&gt;<br>Set Scytales manager configuration directly from a pre-configured instance.<br>[Scytales MID SDK]<br>open override fun [manager](manager.md)(block: [ScytalesManagerConfigBuilder](../../com.scytales.mid.sdk.manager/-scytales-manager-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;<br>Configure Scytales manager settings using a DSL block. |
| [wallet](wallet.md) | [Scytales MID SDK]<br>open override fun [wallet](wallet.md)(config: &lt;Error class: unknown class&gt;): &lt;Error class: unknown class&gt;<br>Set wallet configuration directly from a pre-configured instance.<br>[Scytales MID SDK]<br>open override fun [wallet](wallet.md)(block: &lt;Error class: unknown class&gt;.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;<br>Configure wallet settings using a DSL block. |
