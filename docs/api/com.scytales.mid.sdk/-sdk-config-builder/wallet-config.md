//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkConfigBuilder](index.md)/[walletConfig](wallet-config.md)

# walletConfig

[Scytales MID SDK]\
var [walletConfig](wallet-config.md): &lt;Error class: unknown class&gt;

Configuration for Wallet functionality.

This property holds the EudiWalletConfig instance that configures all standard EUDI Wallet features including document management, issuance, presentation, and security settings. It is initialized with a default EudiWalletConfig instance and can be modified through the [wallet](wallet.md) DSL methods.

The configuration is accessed via the `wallet { }` DSL block and cannot be set directly from outside the builder.
