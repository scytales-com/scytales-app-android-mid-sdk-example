//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[WalletInstanceProvider](index.md)/[getWalletInstance](get-wallet-instance.md)

# getWalletInstance

[Scytales MID SDK]\
abstract suspend fun [getWalletInstance](get-wallet-instance.md)(): [WalletInstance](../-wallet-instance/index.md)

Retrieves the persistent wallet instance attestation for this device.

**This method MUST always return the same wallet instance (with the same cryptographic key) for a given device.** The wallet instance should be created on first call and persisted to secure storage, then retrieved from storage on subsequent calls.

The returned [WalletInstance](../-wallet-instance/index.md) contains the necessary cryptographic proofs and attestations required for secure wallet operations. The instance is used during document issuance to prove the wallet's authenticity to the issuer and to maintain a stable device identity.

Implementation requirements:

- 
   On first call: Generate a new key, store it securely, and return a wallet instance
- 
   On subsequent calls: Retrieve the existing key from secure storage and return it
- 
   The key must persist across app restarts, updates, and other lifecycle events
- 
   The same key must be returned until the app is uninstalled or data is cleared

#### Return

A [WalletInstance](../-wallet-instance/index.md) containing the persistent wallet attestation data with     the same cryptographic key across all invocations.

#### See also

| |
|---|
| [WalletInstance](../-wallet-instance/index.md) |

#### Throws

| | |
|---|---|
| [Exception](https://developer.android.com/reference/kotlin/java/lang/Exception.html) | if wallet instance generation or retrieval fails due to cryptographic     errors, storage failures, platform API failures, or other issues. |
