//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk.manager.wallet](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [DeviceInfo](-device-info/index.md) | [Scytales MID SDK]<br>data class [DeviceInfo](-device-info/index.md)(val os: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot;android&quot;, val osVersion: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = Build.VERSION.RELEASE, val osSDK: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot;&quot;, val manufacturer: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = Build.MANUFACTURER, val model: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = &quot; (&quot;, val locale: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val language: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val timezone: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) = TimeZone.getDefault().id, val appId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val appVersion: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html))<br>Contains metadata and information about the device running the wallet. |
| [WalletInstance](-wallet-instance/index.md) | [Scytales MID SDK]<br>interface [WalletInstance](-wallet-instance/index.md)<br>Represents a persistent wallet instance with cryptographic capabilities. |
| [WalletInstanceProvider](-wallet-instance-provider/index.md) | [Scytales MID SDK]<br>fun interface [WalletInstanceProvider](-wallet-instance-provider/index.md)<br>Functional interface for providing persistent wallet instance attestations. |
