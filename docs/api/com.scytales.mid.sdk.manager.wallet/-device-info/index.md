//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[DeviceInfo](index.md)

# DeviceInfo

[Scytales MID SDK]\
data class [DeviceInfo](index.md)(val os: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot;android&quot;, val osVersion: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = Build.VERSION.RELEASE, val osSDK: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot;&quot;, val manufacturer: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = Build.MANUFACTURER, val model: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot; (&quot;, val locale: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val language: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val timezone: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = TimeZone.getDefault().id, val appId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val appVersion: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html))

Contains metadata and information about the device running the wallet.

This data class captures device-specific information that helps identify and contextualize the wallet instance, including OS details, hardware information, and application metadata.

## Constructors

| | |
|---|---|
| [DeviceInfo](-device-info.md) | [Scytales MID SDK]<br>constructor(os: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot;android&quot;, osVersion: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = Build.VERSION.RELEASE, osSDK: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot;&quot;, manufacturer: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = Build.MANUFACTURER, model: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot; (&quot;, locale: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), language: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), timezone: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = TimeZone.getDefault().id, appId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), appVersion: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [appId](app-id.md) | [Scytales MID SDK]<br>val [appId](app-id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The application package identifier. |
| [appVersion](app-version.md) | [Scytales MID SDK]<br>val [appVersion](app-version.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The application version string. |
| [language](language.md) | [Scytales MID SDK]<br>val [language](language.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The current device language setting. |
| [locale](locale.md) | [Scytales MID SDK]<br>val [locale](locale.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The current device locale setting. |
| [manufacturer](manufacturer.md) | [Scytales MID SDK]<br>val [manufacturer](manufacturer.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The device manufacturer (e.g., &quot;Samsung&quot;, &quot;Google&quot;). |
| [model](model.md) | [Scytales MID SDK]<br>val [model](model.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The device model and hardware identifier. |
| [os](os.md) | [Scytales MID SDK]<br>val [os](os.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The operating system name (defaults to &quot;android&quot;). |
| [osSDK](os-s-d-k.md) | [Scytales MID SDK]<br>val [osSDK](os-s-d-k.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The Android SDK level as a string (e.g., &quot;34&quot;, &quot;33&quot;). |
| [osVersion](os-version.md) | [Scytales MID SDK]<br>val [osVersion](os-version.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The OS version string (e.g., &quot;14&quot;, &quot;13&quot;). |
| [timezone](timezone.md) | [Scytales MID SDK]<br>val [timezone](timezone.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>The device timezone identifier (e.g., &quot;America/New_York&quot;). |
