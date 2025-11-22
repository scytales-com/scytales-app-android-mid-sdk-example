//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[WalletInstance](index.md)

# WalletInstance

interface [WalletInstance](index.md)

Represents a persistent wallet instance with cryptographic capabilities.

A wallet instance provides a stable identity for a device through a persistent cryptographic key. **The wallet instance MUST always return the same key for a given device** to ensure consistent identification with the backend. This persistence is critical for:

- 
   Device identification and authentication
- 
   Secure communication with the manager backend
- 
   Document issuance and verification flows

The wallet instance encapsulates:

- 
   A persistent cryptographic key pair (stored securely in the device's secure area)
- 
   Device information and metadata
- 
   Signing capabilities for attestations

Implementations must ensure that:

1. 
   The same key is used across all app sessions and restarts
2. 
   The key is stored in the device's secure storage (e.g., Android Keystore)
3. 
   The wallet ID derived from the key remains constant

#### See also

| |
|---|
| [WalletInstanceProvider](../-wallet-instance-provider/index.md) |
| [DeviceInfo](../-device-info/index.md) |

## Functions

| Name | Summary |
|---|---|
| [getDeviceInfo](get-device-info.md) | [Scytales MID SDK]<br>abstract suspend fun [getDeviceInfo](get-device-info.md)(): [DeviceInfo](../-device-info/index.md)<br>Retrieves the device information and metadata. |
| [getKeyInfo](get-key-info.md) | [Scytales MID SDK]<br>abstract suspend fun [getKeyInfo](get-key-info.md)(): &lt;Error class: unknown class&gt;<br>Retrieves the cryptographic key information for this wallet instance. |
| [getWalletId](get-wallet-id.md) | [Scytales MID SDK]<br>abstract suspend fun [getWalletId](get-wallet-id.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Retrieves the unique wallet identifier for this device. |
| [sign](sign.md) | [Scytales MID SDK]<br>abstract suspend fun [sign](sign.md)(dataToSign: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Signs data using the wallet instance's private key. |
