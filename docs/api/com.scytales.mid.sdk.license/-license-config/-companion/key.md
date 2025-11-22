//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.license](../../index.md)/[LicenseConfig](../index.md)/[Companion](index.md)/[key](key.md)

# key

[Scytales MID SDK]\
fun [key](key.md)(licenseKey: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), cacheLocation: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)? = null): [LicenseConfig.WithKey](../-with-key/index.md)

Creates a key-based license configuration.

This factory method creates a [WithKey](../-with-key/index.md) instance that uses a license key string for SDK activation. The license key is validated during SDK initialization.

#### Return

A [WithKey](../-with-key/index.md) configuration instance

#### Parameters

Scytales MID SDK

| | |
|---|---|
| licenseKey | The license key string provided by your license provider.     Must not be blank. |
| cacheLocation | Optional directory path for caching license data. If provided,     license information will be cached to improve subsequent     startup performance. If null, no caching is performed. |

#### Throws

| | |
|---|---|
| [IllegalArgumentException](https://developer.android.com/reference/kotlin/java/lang/IllegalArgumentException.html) | if the license key is blank<br>Example:<br>```kotlin // Without caching val config = LicenseConfig.key("your-license-key")<br>// With caching val config = LicenseConfig.key(     licenseKey = "your-license-key",     cacheLocation = context.cacheDir.absolutePath ) ``` |
