//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.license](../../index.md)/[LicenseConfig](../index.md)/[WithKey](index.md)

# WithKey

class [WithKey](index.md) : [LicenseConfig](../index.md)

License configuration using a license key string.

This configuration method uses a license key provided as a string, typically obtained from your license provider. The license key is validated during SDK initialization and may be cached locally to improve startup performance.

Key-based licensing is recommended when:

- 
   You want to manage licenses remotely
- 
   You need to support license key rotation
- 
   You prefer storing a simple string rather than binary data

#### See also

| | |
|---|---|
| [LicenseConfig.Companion.key](../-companion/key.md) | for creating instances<br>Example:<br>```kotlin val config = LicenseConfig.key(     licenseKey = "XXXX-XXXX-XXXX-XXXX",     cacheLocation = context.cacheDir.absolutePath ) ``` |

## Properties

| Name | Summary |
|---|---|
| [cacheLocation](cache-location.md) | [Scytales MID SDK]<br>val [cacheLocation](cache-location.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Optional directory path for caching license data to improve     startup performance. If null, no caching is performed. |
| [licenseKey](license-key.md) | [Scytales MID SDK]<br>val [licenseKey](license-key.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The license key string provided by your license provider |
