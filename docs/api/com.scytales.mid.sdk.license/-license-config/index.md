//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.license](../index.md)/[LicenseConfig](index.md)

# LicenseConfig

sealed interface [LicenseConfig](index.md)

Configuration for SDK license activation and validation.

Represents the license information required to activate and use the SDK. The license ensures that SDK features are properly authorized and tracks usage according to the license agreement. Two configuration methods are supported:

- 
   **Key-based licensing** via [WithKey](-with-key/index.md) - Uses a license key string with optional caching
- 
   **Content-based licensing** via [WithContents](-with-contents/index.md) - Uses raw license file contents

License validation occurs during SDK initialization. If validation fails, the SDK will throw an exception and prevent access to SDK features.

#### See also

| | |
|---|---|
| [LicenseConfig.WithKey](-with-key/index.md) | for key-based license configuration |
| [LicenseConfig.WithContents](-with-contents/index.md) | for content-based license configuration |
| [Sdk](../../com.scytales.mid.sdk/-sdk/index.md) |
| [SdkBuilder](../../com.scytales.mid.sdk/-sdk-builder/index.md) | Example usage with license key:<br>```kotlin val licenseConfig = LicenseConfig.key(     licenseKey = "your-license-key-here",     cacheLocation = context.cacheDir.absolutePath )<br>val sdk = SdkBuilder(context)     .license(licenseConfig)     .build() ```<br>Example usage with license contents:<br>```kotlin val licenseBytes = context.assets.open("license.dat").readBytes() val licenseConfig = LicenseConfig.contents(licenseBytes)<br>val sdk = SdkBuilder(context)     .license(licenseConfig)     .build() ``` |

#### Inheritors

| |
|---|
| [WithKey](-with-key/index.md) |
| [WithContents](-with-contents/index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [Scytales MID SDK]<br>object [Companion](-companion/index.md)<br>Factory methods for creating [LicenseConfig](index.md) instances. |
| [WithContents](-with-contents/index.md) | [Scytales MID SDK]<br>value class [WithContents](-with-contents/index.md) : [LicenseConfig](index.md)<br>License configuration using raw license file contents. |
| [WithKey](-with-key/index.md) | [Scytales MID SDK]<br>class [WithKey](-with-key/index.md) : [LicenseConfig](index.md)<br>License configuration using a license key string. |
