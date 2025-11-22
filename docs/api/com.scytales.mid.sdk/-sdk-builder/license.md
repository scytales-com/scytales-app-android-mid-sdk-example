//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkBuilder](index.md)/[license](license.md)

# license

[Scytales MID SDK]\
fun [license](license.md)(license: [LicenseConfig](../../com.scytales.mid.sdk.license/-license-config/index.md)): &lt;Error class: unknown class&gt;

Sets the license configuration for the SDK.

This method follows the builder pattern, returning the builder instance to allow method chaining.

#### Return

This builder instance for method chaining

Example:

```kotlin
builder.license(LicenseConfig.fromFile("license.json"))
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| license | The license configuration to use for SDK initialization |

[Scytales MID SDK]\
var [license](license.md): [LicenseConfig](../../com.scytales.mid.sdk.license/-license-config/index.md)?

The license configuration for the SDK.

This property holds the license configuration that will be validated before the SDK is built. It must be set before calling [build](build.md), otherwise an [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) will be thrown.

#### See also

| | |
|---|---|
| [SdkBuilder.license](license.md) | for setting this value using the builder pattern |
