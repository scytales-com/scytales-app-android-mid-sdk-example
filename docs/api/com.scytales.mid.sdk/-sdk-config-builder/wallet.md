//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkConfigBuilder](index.md)/[wallet](wallet.md)

# wallet

[Scytales MID SDK]\
open override fun [wallet](wallet.md)(block: &lt;Error class: unknown class&gt;.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;

Configure wallet settings using a DSL block.

This method provides direct access to the EudiWalletConfig instance, allowing you to configure all core wallet functionality using the EUDI Wallet Core configuration API.

Configuration options include:

- 
   Document storage paths and database configuration
- 
   Security settings for key creation and management
- 
   OpenID4VCI issuance endpoints and authentication
- 
   Proximity presentation (BLE/NFC) settings
- 
   Remote presentation (OpenID4VP) configuration
- 
   Reader certificate trust store
- 
   Document status resolution and revocation checking
- 
   Logging levels and output

The configuration block is applied directly to the internal [walletConfig](wallet-config.md) instance.

#### Return

This builder instance for method chaining

Example:

```kotlin
wallet {
    configureDocumentManager("/path/to/storage.db")
    configureLogging(level = Logger.LEVEL_DEBUG)
    configureDocumentKeyCreation(
        userAuthenticationRequired = true,
        userAuthenticationTimeout = 30_000.milliseconds
    )
}
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | Configuration lambda with EudiWalletConfig as receiver |

[Scytales MID SDK]\
open override fun [wallet](wallet.md)(config: &lt;Error class: unknown class&gt;): &lt;Error class: unknown class&gt;

Set wallet configuration directly from a pre-configured instance.

Use this method when you have constructed an EudiWalletConfig instance separately and want to use it as-is rather than building it through the DSL. This is useful for sharing configurations across multiple SDK instances or when migrating from existing configuration objects.

#### Return

This builder instance for method chaining

Example:

```kotlin
val sharedWalletConfig = EudiWalletConfig().apply {
    configureDocumentManager("/path/to/storage.db")
    // ... other configuration
}

wallet(sharedWalletConfig)
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| config | Pre-configured EudiWalletConfig instance |
