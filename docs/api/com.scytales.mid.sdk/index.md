//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [Sdk](-sdk/index.md) | [Scytales MID SDK]<br>interface [Sdk](-sdk/index.md) : [ScytalesManager](../com.scytales.mid.sdk.manager/-scytales-manager/index.md)<br>Main interface for the Scytales MID SDK. |
| [SdkBuilder](-sdk-builder/index.md) | [Scytales MID SDK]<br>class [SdkBuilder](-sdk-builder/index.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) : CommonBuilder, [Sdk.Builder](-sdk/-builder/index.md)<br>Builder class for constructing [Sdk](-sdk/index.md) instances with license validation. |
| [SdkConfig](-sdk-config/index.md) | [Scytales MID SDK]<br>data class [SdkConfig](-sdk-config/index.md)(val walletConfig: &lt;Error class: unknown class&gt;, val managerConfig: [ScytalesManagerConfig](../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md))<br>Configuration container for the Scytales MID SDK. |
| [SdkConfigBuilder](-sdk-config-builder/index.md) | [Scytales MID SDK]<br>class [SdkConfigBuilder](-sdk-config-builder/index.md) : [SdkConfig.Builder](-sdk-config/-builder/index.md)<br>Implementation of [SdkConfig.Builder](-sdk-config/-builder/index.md) that provides a DSL for constructing SDK configurations. |

## Functions

| Name | Summary |
|---|---|
| [sdkConfig](sdk-config.md) | [Scytales MID SDK]<br>suspend fun [sdkConfig](sdk-config.md)(block: [SdkConfig.Builder](-sdk-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SdkConfig](-sdk-config/index.md)<br>Creates a new [SdkConfig](-sdk-config/index.md) using a DSL builder. |
