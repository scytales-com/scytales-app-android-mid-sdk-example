//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[WalletInstanceProvider](index.md)

# WalletInstanceProvider

fun interface [WalletInstanceProvider](index.md)

Functional interface for providing persistent wallet instance attestations.

A wallet instance represents a cryptographically verifiable attestation of the wallet's authenticity and integrity. **The provider MUST always return the same wallet instance (with the same cryptographic key) for a given device** to ensure consistent identification with the manager backend.

Key requirements:

- 
   The wallet instance must persist across app sessions, restarts, and updates
- 
   The same cryptographic key must be used for the lifetime of the app installation
- 
   The wallet ID derived from the key must remain constant
- 
   The key should be stored in the device's secure storage (e.g., Android Keystore)

Implementations should handle:

- 
   Persistent wallet attestation generation and retrieval
- 
   Key management and secure storage for signing attestations
- 
   Interaction with platform-specific attestation APIs (e.g., Android KeyAttestation)
- 
   Key creation on first use and retrieval on subsequent calls

Example usage:

```kotlin
val provider = WalletInstanceProvider {
    // Retrieve or create a persistent wallet instance
    // This will return the SAME instance every time
    myWalletInstanceGenerator.getOrCreateInstance()
}

val instance1 = provider.getWalletInstance()
val instance2 = provider.getWalletInstance()
// instance1 and instance2 must have the same key and wallet ID
```

#### See also

| |
|---|
| [WalletInstance](../-wallet-instance/index.md) |

## Functions

| Name | Summary |
|---|---|
| [getWalletInstance](get-wallet-instance.md) | [Scytales MID SDK]<br>abstract suspend fun [getWalletInstance](get-wallet-instance.md)(): [WalletInstance](../-wallet-instance/index.md)<br>Retrieves the persistent wallet instance attestation for this device. |
