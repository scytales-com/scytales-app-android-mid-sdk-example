//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[SdkConfig](../index.md)/[Builder](index.md)/[wallet](wallet.md)

# wallet

[Scytales MID SDK]\
abstract fun [wallet](wallet.md)(block: &lt;Error class: unknown class&gt;.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SdkConfig.Builder](index.md)

Configure wallet settings using a DSL block.

This method provides access to all EudiWalletConfig configuration options including:

- 
   Document storage and key management
- 
   OpenID4VCI document issuance
- 
   Proximity presentation (BLE/NFC)
- 
   Remote presentation (OpenID4VP)
- 
   Reader trust store
- 
   Document status resolution
- 
   Logging configuration

#### Return

This builder instance for method chaining

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | Configuration lambda with EudiWalletConfig as receiver |

[Scytales MID SDK]\
abstract fun [wallet](wallet.md)(config: &lt;Error class: unknown class&gt;): [SdkConfig.Builder](index.md)

Set wallet configuration directly.

Use this method if you have already constructed an EudiWalletConfig instance elsewhere and want to use it directly instead of configuring it via the DSL.

#### Return

This builder instance for method chaining

#### Parameters

Scytales MID SDK

| | |
|---|---|
| config | Pre-configured EudiWalletConfig instance |
