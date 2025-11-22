//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[WalletInstance](index.md)/[sign](sign.md)

# sign

[Scytales MID SDK]\
abstract suspend fun [sign](sign.md)(dataToSign: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Signs data using the wallet instance's private key.

This method creates a cryptographic signature over the provided data using the wallet's persistent private key. The signature can be verified using the public key from [getKeyInfo](get-key-info.md).

#### Return

The signature bytes in COSE Sign1 format.

#### Parameters

Scytales MID SDK

| | |
|---|---|
| dataToSign | The data to be signed. |

#### Throws

| | |
|---|---|
| [Exception](https://developer.android.com/reference/kotlin/java/lang/Exception.html) | if signing fails due to cryptographic errors or key unavailability. |
