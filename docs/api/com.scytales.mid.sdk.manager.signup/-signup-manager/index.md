//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.signup](../index.md)/[SignupManager](index.md)

# SignupManager

fun interface [SignupManager](index.md)

Functional interface for managing user signup and document issuance flows.

The SignupManager handles the complete process of enrolling new users and issuing digital identity documents to their wallets. It orchestrates the interaction between the user, the issuer, and the wallet to securely provision credentials.

The signup process typically includes:

- 
   User authentication and verification with the issuer
- 
   Document type selection from available templates
- 
   Credential issuance and secure storage in the wallet
- 
   Wallet instance attestation for device authentication

Example usage:

```kotlin
val signupManager = scytalesManager.createSignManager()
val availableTypes = scytalesManager.getAvailableDocumentTypes().getOrThrow()
val documentType = availableTypes.first()

signupManager.issueDocument(activity, documentType)
    .onSuccess { issuedDoc ->
        // Document successfully issued and stored
    }
    .onFailure { error ->
        // Handle issuance error
    }
```

#### See also

| |
|---|
| SignupManagerBuilder |
| [AvailableDocumentType](../../com.scytales.mid.sdk.manager/-available-document-type/index.md) |
| IssuedDocument |

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [Scytales MID SDK]<br>interface [Builder](-builder/index.md)<br>Builder interface for constructing [SignupManager](index.md) instances. |

## Functions

| Name | Summary |
|---|---|
| [issueDocument](issue-document.md) | [Scytales MID SDK]<br>abstract suspend fun [issueDocument](issue-document.md)(activity: &lt;Error class: unknown class&gt;, documentType: [AvailableDocumentType](../../com.scytales.mid.sdk.manager/-available-document-type/index.md)): &lt;Error class: unknown class&gt;&lt;&lt;Error class: unknown class&gt;&gt;<br>Issues a digital identity document to the user's wallet. |
