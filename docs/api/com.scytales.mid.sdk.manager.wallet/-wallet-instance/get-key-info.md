//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[WalletInstance](index.md)/[getKeyInfo](get-key-info.md)

# getKeyInfo

[Scytales MID SDK]\
abstract suspend fun [getKeyInfo](get-key-info.md)(): &lt;Error class: unknown class&gt;

Retrieves the cryptographic key information for this wallet instance.

**This must always return the same key for a given device.** The key is persistently stored and should survive app restarts, updates, and other lifecycle events.

#### Return

The KeyInfo containing the public key, attestation, and other key metadata.
