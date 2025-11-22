//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[WalletInstance](index.md)/[getWalletId](get-wallet-id.md)

# getWalletId

[Scytales MID SDK]\
abstract suspend fun [getWalletId](get-wallet-id.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)

Retrieves the unique wallet identifier for this device.

The wallet ID is derived from the wallet's public key (typically a hash of the key) and **must remain constant for the lifetime of the wallet instance on the device**. This ID is used by the backend to identify and track the device.

#### Return

A unique, stable identifier string for this wallet instance.
