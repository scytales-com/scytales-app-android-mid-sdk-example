//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.signup](../index.md)/[SignupManager](index.md)/[issueDocument](issue-document.md)

# issueDocument

[Scytales MID SDK]\
abstract suspend fun [issueDocument](issue-document.md)(activity: &lt;Error class: unknown class&gt;, documentType: [AvailableDocumentType](../../com.scytales.mid.sdk.manager/-available-document-type/index.md)): &lt;Error class: unknown class&gt;&lt;&lt;Error class: unknown class&gt;&gt;

Issues a digital identity document to the user's wallet.

This method initiates the document issuance flow, which includes:

1. 
   Launching the issuer's authentication and enrollment UI
2. 
   Collecting necessary user information and consent
3. 
   Generating cryptographic keys for the document
4. 
   Requesting the document from the issuer
5. 
   Storing the issued document securely in the wallet

The method is asynchronous and may present UI to the user for authentication, biometric verification, or data collection. The calling activity is used to launch these UI flows.

#### Return

A Result containing the IssuedDocument on success, or an error     if the issuance process fails. Possible failure reasons include:     - User cancellation or denial     - Authentication failures     - Network errors     - Issuer rejection     - Cryptographic errors     - Storage failures

#### Parameters

Scytales MID SDK

| | |
|---|---|
| activity | The ComponentActivity used to launch UI flows during the     issuance process. This activity will be used to start     authentication activities and handle results. |
| documentType | The [AvailableDocumentType](../../com.scytales.mid.sdk.manager/-available-document-type/index.md) specifying which type of     document to issue. This must be one of the available     document types for the configured organization. |

#### See also

| |
|---|
| [AvailableDocumentType](../../com.scytales.mid.sdk.manager/-available-document-type/index.md) |
| IssuedDocument |
