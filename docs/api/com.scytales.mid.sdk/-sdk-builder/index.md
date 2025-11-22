//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkBuilder](index.md)

# SdkBuilder

class [SdkBuilder](index.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) : CommonBuilder, [Sdk.Builder](../-sdk/-builder/index.md)

Builder class for constructing [Sdk](../-sdk/index.md) instances with license validation.

Provides a fluent API for configuring and creating SDK instances. It enforces license configuration and validates the license before building the SDK.

The builder follows the builder pattern, allowing method chaining for configuration:

- 
   License configuration must be provided before building
- 
   License is validated during the build process
- 
   Build operation is suspending to support asynchronous license evaluation

#### Parameters

Scytales MID SDK

| | |
|---|---|
| context | The Android application context used for SDK initialization and license validation |

#### See also

| | |
|---|---|
| [Sdk](../-sdk/index.md) | for the main SDK interface |
| [LicenseConfig](../../com.scytales.mid.sdk.license/-license-config/index.md) | for license configuration options<br>Example usage:<br>```kotlin val sdk = SdkBuilder(context)     .license(LicenseConfig.fromFile("path/to/license.json"))     .build() ``` |

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if [build](build.md) is called without setting a license configuration |

## Constructors

| | |
|---|---|
| [SdkBuilder](-sdk-builder.md) | [Scytales MID SDK]<br>constructor(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) |

## Properties

| Name | Summary |
|---|---|
| [license](license.md) | [Scytales MID SDK]<br>var [license](license.md): [LicenseConfig](../../com.scytales.mid.sdk.license/-license-config/index.md)?<br>The license configuration for the SDK. |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>open suspend override fun [build](build.md)(): [Sdk](../-sdk/index.md)<br>Builds and returns a configured [Sdk](../-sdk/index.md) instance. |
| [configure](index.md#361808558%2FFunctions%2F782143165) | [Scytales MID SDK]<br>open override fun [configure](index.md#361808558%2FFunctions%2F782143165)(configure: [SdkConfig.Builder](../-sdk-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](../-sdk/-builder/index.md)<br>Configure the SDK using a unified configuration DSL. |
| [customizeManager](index.md#1322968122%2FFunctions%2F782143165) | [Scytales MID SDK]<br>open override fun [customizeManager](index.md#1322968122%2FFunctions%2F782143165)(customize: [ScytalesManager.Builder](../../com.scytales.mid.sdk.manager/-scytales-manager/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](../-sdk/-builder/index.md)<br>Customize the Scytales manager with advanced settings. |
| [customizeWallet](index.md#-2030371289%2FFunctions%2F782143165) | [Scytales MID SDK]<br>open override fun [customizeWallet](index.md#-2030371289%2FFunctions%2F782143165)(customize: &lt;Error class: unknown class&gt;.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](../-sdk/-builder/index.md)<br>Customize the Wallet with advanced component overrides. |
| [license](license.md) | [Scytales MID SDK]<br>fun [license](license.md)(license: [LicenseConfig](../../com.scytales.mid.sdk.license/-license-config/index.md)): &lt;Error class: unknown class&gt;<br>Sets the license configuration for the SDK. |
