//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.license](../../index.md)/[LicenseConfig](../index.md)/[Companion](index.md)/[contents](contents.md)

# contents

[Scytales MID SDK]\
fun [contents](contents.md)(licenseContents: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [LicenseConfig.WithContents](../-with-contents/index.md)

Creates a content-based license configuration.

This factory method creates a [WithContents](../-with-contents/index.md) instance that uses the raw binary contents of a license file for SDK activation. The license data is validated during SDK initialization.

#### Return

A [WithContents](../-with-contents/index.md) configuration instance

#### Parameters

Scytales MID SDK

| | |
|---|---|
| licenseContents | The raw binary contents of the license file.     Must not be empty. |

#### Throws

| | |
|---|---|
| [IllegalArgumentException](https://developer.android.com/reference/kotlin/java/lang/IllegalArgumentException.html) | if the license contents are empty<br>Example:<br>```kotlin // From assets val licenseBytes = context.assets.open("license.dat").readBytes() val config = LicenseConfig.contents(licenseBytes)<br>// From file val licenseFile = File(context.filesDir, "license.dat") val config = LicenseConfig.contents(licenseFile.readBytes()) ``` |
