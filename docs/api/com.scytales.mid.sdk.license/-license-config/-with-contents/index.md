//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.license](../../index.md)/[LicenseConfig](../index.md)/[WithContents](index.md)

# WithContents

value class [WithContents](index.md) : [LicenseConfig](../index.md)

License configuration using raw license file contents.

This configuration method uses the raw binary contents of a license file, typically stored as an asset or resource in your application. The license data is validated during SDK initialization.

Content-based licensing is recommended when:

- 
   You want to bundle the license directly with your application
- 
   You need offline license validation without network connectivity
- 
   You prefer not to expose license keys in code or configuration

#### See also

| | |
|---|---|
| [LicenseConfig.Companion.contents](../-companion/contents.md) | for creating instances<br>Example:<br>```kotlin val licenseBytes = context.assets.open("license.dat").readBytes() val config = LicenseConfig.contents(licenseBytes) ``` |

## Properties

| Name | Summary |
|---|---|
| [licenseContents](license-contents.md) | [Scytales MID SDK]<br>val [licenseContents](license-contents.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>The raw binary contents of the license file |
