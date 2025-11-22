//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.manager.signup](../../index.md)/[SignupManager](../index.md)/[Builder](index.md)/[walletInstanceProvider](wallet-instance-provider.md)

# walletInstanceProvider

[Scytales MID SDK]\
abstract fun [walletInstanceProvider](wallet-instance-provider.md)(provider: [WalletInstanceProvider](../../../com.scytales.mid.sdk.manager.wallet/-wallet-instance-provider/index.md)): [SignupManager.Builder](index.md)

Sets the wallet instance provider for device attestation.

The wallet instance provider supplies the persistent device identity and attestation capabilities used during the issuance process to prove the wallet's authenticity to the issuer.

#### Return

This builder instance for method chaining.

#### Parameters

Scytales MID SDK

| | |
|---|---|
| provider | The [WalletInstanceProvider](../../../com.scytales.mid.sdk.manager.wallet/-wallet-instance-provider/index.md) to use for wallet instance     operations and device attestation. |
