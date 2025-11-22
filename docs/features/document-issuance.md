# Document Issuance

Issue digital credentials using the Scytales MID SDK.

## Overview

The SDK supports two methods for issuing digital credentials:

1. **Scytales Manager**: Direct issuance via organization backend with biometric enrollment
2. **OpenID4VCI**: Standards-based credential offer protocol with QR code scanning

Both methods support multiple credential formats (ISO mDL, SD-JWT-VC) and store documents securely in the wallet.

## Issuance Methods Comparison

| Feature | Scytales Manager | OpenID4VCI |
|---------|-----------------|------------|
| **User Flow** | Integrated enrollment + issuance | Scan QR code + authorize |
| **Biometrics** | ✅ FaceTec face scan | ❌ Not supported |
| **Configuration** | Organization ID required | Issuer URL required |
| **Standards** | Scytales proprietary | OpenID4VCI 1.0 |
| **Use Case** | First-time enrollment | Subsequent issuance |

---

## Method 1: Scytales Manager

Issue documents directly through your organization's backend with integrated biometric enrollment.

### Prerequisites

- Organization ID configured in `SdkConfig`
- Optional: FaceTec keys for biometric enrollment
- User authenticated with your organization

### User Flow

1. User selects document type to issue
2. Authenticates with issuer (if required)
3. Completes enrollment form
4. Performs biometric face scan (if configured)
5. Reviews and confirms
6. Document issued and stored in wallet

### Implementation

#### Step 1: Get Available Document Types

Retrieve document types available for issuance:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()

sdk.getAvailableDocumentTypes()
    .onSuccess { documentTypes ->
        documentTypes.forEach { docType ->
            println("Available: ${docType.template.name}")
        }
    }
    .onFailure { error ->
        Log.e(TAG, "Failed to get document types", error)
    }
```

**Response**: `List<AvailableDocumentType>` with available document templates.

#### Step 2: Create Signup Manager

Create a manager for the issuance flow:

```kotlin
val signupManager = sdk.createSignManager()
```

#### Step 3: Issue Document

Launch the issuance flow:

```kotlin
signupManager.issueDocument(activity, documentType)
    .onSuccess { issuedDocument ->
        Log.d(TAG, "Document issued: ${issuedDocument.id}")
    }
    .onFailure { error ->
        Log.e(TAG, "Issuance failed", error)
    }
```

**Parameters:**
- `activity`: `ComponentActivity` for launching UI flows
- `documentType`: Selected `AvailableDocumentType`

**Returns**: `Result<IssuedDocument>` with the issued document or error.

### Complete Example

```kotlin
class DocumentTypesViewModel : ViewModel() {
    
    fun loadDocumentTypes() {
        viewModelScope.launch {
            _state.value = DocumentTypesState.Loading
            
            ScytalesSdkInitializer.getSdk()
                .getAvailableDocumentTypes()
                .onSuccess { types ->
                    _state.value = DocumentTypesState.Success(types)
                }
                .onFailure { error ->
                    _state.value = DocumentTypesState.Error(error.message)
                }
        }
    }
}

class DocumentIssuanceViewModel : ViewModel() {
    
    fun issueDocument(
        activity: ComponentActivity,
        documentType: AvailableDocumentType
    ) {
        viewModelScope.launch {
            _state.value = IssuanceState.Issuing
            
            val sdk = ScytalesSdkInitializer.getSdk()
            val signupManager = sdk.createSignManager()
            
            signupManager.issueDocument(activity, documentType)
                .onSuccess { document ->
                    _state.value = IssuanceState.Success(document)
                }
                .onFailure { error ->
                    _state.value = IssuanceState.Error(error.message)
                }
        }
    }
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/manager/)

### Issuance Process

The `issueDocument()` method handles the complete flow:

1. **Issuer Authentication**: Launches issuer login UI (OpenID Connect)
2. **Enrollment Form**: Collects user information (name, date of birth, etc.)
3. **Biometric Capture**: Performs FaceTec face scan (if configured)
4. **Document Request**: Requests credential from issuer backend
5. **Key Generation**: Creates cryptographic keys in Android Keystore
6. **Document Storage**: Stores document securely in wallet

### UI Integration

The SDK provides pre-built UI components for the issuance flow. Your activity must be a `ComponentActivity` to support Compose integration.

### Error Handling

Common errors:

| Error | Cause | Solution |
|-------|-------|----------|
| `OrganizationNotConfigured` | No organization ID in config | Add organization ID to `SdkConfig` |
| `AuthenticationFailed` | User cancelled or auth failed | Retry with valid credentials |
| `BiometricFailed` | Face scan failed | Ensure good lighting, retry |
| `IssuerError` | Backend rejected request | Check user eligibility |

---

## Method 2: OpenID4VCI

Issue documents using the OpenID for Verifiable Credential Issuance protocol.

### Prerequisites

- OpenID4VCI configured in SDK initialization
- Credential offer URI (from QR code or deep link)
- Optional: Transaction code (PIN) for secure issuance

### User Flow

1. User scans QR code containing credential offer
2. App resolves offer to display credential details
3. User reviews and accepts offer
4. Browser opens for issuer authentication (if required)
5. App receives authorization callback
6. Credentials issued and stored in wallet

### Credential Offer Format

Credential offers are URIs in one of two formats:

```
openid-credential-offer://?credential_offer_uri=https://issuer.example.com/offers/abc123
```

```
openid-credential-offer://?credential_offer={"credential_issuer":"https://issuer.example.com",...}
```

### Implementation

#### Step 1: Create OpenId4VCI Manager

Create a manager for OpenID4VCI operations:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()
val openId4VciManager = sdk.createOpenId4VciManager()
```

#### Step 2: Resolve Credential Offer

Resolve the offer URI to retrieve offer details:

```kotlin
openId4VciManager.resolveDocumentOffer(offerUri) { result ->
    when (result) {
        is OfferResult.Success -> {
            val offer = result.offer
            // Display offer details to user
        }
        is OfferResult.Failure -> {
            Log.e(TAG, "Failed to resolve offer", result.cause)
        }
    }
}
```

**Parameters:**
- `offerUri`: The credential offer URI (from QR code)
- `callback`: Lambda receiving `OfferResult`

**Response**: `Offer` object with credential details or error.

#### Step 3: Issue Documents from Offer

Issue the credentials from the resolved offer:

```kotlin
openId4VciManager.issueDocumentByOffer(
    offer = offer,
    txCode = null // or user-entered PIN
) { event ->
    when (event) {
        is IssueEvent.Started -> {
            Log.d(TAG, "Issuance started")
        }
        
        is IssueEvent.DocumentRequiresUserAuth -> {
            Log.d(TAG, "User authentication required")
        }
        
        is IssueEvent.DocumentRequiresCreateSettings -> {
            val settings = event.getDefaultCreateDocumentSettings()
            event.resume(settings)
        }
        
        is IssueEvent.DocumentIssued -> {
            Log.d(TAG, "Document issued: ${event.documentId}")
        }
        
        is IssueEvent.DocumentFailed -> {
            Log.e(TAG, "Document failed: ${event.name}", event.cause)
        }
        
        is IssueEvent.DocumentDeferred -> {
            Log.d(TAG, "Document deferred: ${event.documentId}")
        }
        
        is IssueEvent.Finished -> {
            Log.d(TAG, "Issuance finished")
        }
        
        is IssueEvent.Failure -> {
            Log.e(TAG, "Issuance failed", event.cause)
        }
    }
}
```

**Parameters:**
- `offer`: Resolved `Offer` object
- `txCode`: Optional transaction code (PIN) for secure issuance
- `callback`: Lambda receiving `IssueEvent` updates

### Issue Events

The issuance process emits events:

| Event | Description | Action Required |
|-------|-------------|-----------------|
| `Started` | Issuance started | Show progress UI |
| `DocumentRequiresUserAuth` | Biometric auth needed | Prompt user for auth |
| `DocumentRequiresCreateSettings` | Document config needed | Call `event.resume()` with settings |
| `DocumentIssued` | Document successfully issued | Update UI |
| `DocumentFailed` | Document issuance failed | Show error |
| `DocumentDeferred` | Document deferred by issuer | Poll for status later |
| `Finished` | All documents processed | Navigate to success |
| `Failure` | Overall issuance failed | Show error and retry |

### Authorization Flow

For credential offers requiring authorization:

#### Step 1: Handle Authorization Request

When `IssueEvent` includes authorization requirement, the SDK opens the browser automatically. No action needed.

#### Step 2: Handle Authorization Redirect

When the browser redirects back to your app, resume the issuance:

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    
    intent.data?.let { uri ->
        if (uri.scheme == "eudi-openid4ci" && uri.host == "authorize") {
            openId4VciManager.resumeWithAuthorization(uri)
        }
    }
}
```

### Complete Example with Flow Management

For handling configuration changes during authorization, use a coordinator pattern:

```kotlin
object OpenId4VciCoordinator {
    private var manager: OpenId4VciManager? = null
    private var activeIssueFlow: Flow<IssueEvent>? = null
    
    fun issueDocuments(offer: Offer, txCode: String?): Flow<IssueEvent> {
        activeIssueFlow?.let { return it }
        
        val flow = callbackFlow {
            getOrCreateManager().issueDocumentByOffer(offer, txCode) { event ->
                trySend(event)
                if (event is IssueEvent.Finished || event is IssueEvent.Failure) {
                    close()
                }
            }
            awaitClose()
        }.shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            replay = 10
        )
        
        activeIssueFlow = flow
        return flow
    }
    
    fun resumeWithAuthorization(uri: Uri): Boolean {
        return manager?.let {
            it.resumeWithAuthorization(uri)
            true
        } ?: false
    }
}
```

[View full coordinator implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/openid4vci/OpenId4VciCoordinator.kt)

### ViewModel Integration

```kotlin
class OpenId4VciViewModel : ViewModel() {
    
    private val _issuanceProgress = MutableStateFlow<IssuanceProgress>(IssuanceProgress.Idle)
    val issuanceProgress: StateFlow<IssuanceProgress> = _issuanceProgress.asStateFlow()
    
    fun issueDocuments(offer: Offer, txCode: String?) {
        viewModelScope.launch {
            OpenId4VciCoordinator.issueDocuments(offer, txCode)
                .collect { event ->
                    _issuanceProgress.value = when (event) {
                        is IssueEvent.Started -> IssuanceProgress.Started
                        is IssueEvent.DocumentIssued -> IssuanceProgress.DocumentIssued(event.documentId)
                        is IssueEvent.Finished -> IssuanceProgress.Finished
                        is IssueEvent.Failure -> IssuanceProgress.Error(event.cause.message)
                        else -> _issuanceProgress.value
                    }
                }
        }
    }
}
```

[View full ViewModel](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/openid4vci/OpenId4VciViewModel.kt)

### QR Code Scanning

To scan credential offer QR codes, use ML Kit barcode scanning:

```kotlin
implementation(libs.mlkit.barcode.scanning)
implementation(libs.androidx.camera.camera2)
implementation(libs.androidx.camera.lifecycle)
implementation(libs.androidx.camera.view)
```

[View QR scanner implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/common/QRScannerScreen.kt)

### Error Handling

Common errors:

| Error | Cause | Solution |
|-------|-------|----------|
| `InvalidOffer` | Malformed offer URI | Verify QR code format |
| `IssuerUnreachable` | Network error | Check connectivity |
| `AuthorizationFailed` | User cancelled auth | Retry authorization |
| `InvalidTxCode` | Wrong PIN | Prompt for correct code |
| `UnsupportedFormat` | Format not configured | Enable format in config |

---

## Issued Document Format

Both methods return an `IssuedDocument` with:

- **ID**: Unique document identifier
- **Format**: mso_mdoc or SD-JWT-VC
- **DocType**: Document type (e.g., `org.iso.18013.5.1.mDL`)
- **Claims**: Document attributes (name, date of birth, etc.)
- **Created At**: Issuance timestamp
- **Key Material**: Cryptographic keys in Android Keystore

### Accessing Issued Documents

After issuance, retrieve documents:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()
val documents = sdk.getDocuments()

documents.forEach { document ->
    if (document is IssuedDocument) {
        println("Document: ${document.id}")
        println("Type: ${document.docType}")
    }
}
```

## Next Steps

- [**Document Presentation**](document-presentation.md) - Present credentials to verifiers
- [**Document Management**](document-management.md) - View and manage issued documents

## Reference

- [**DocumentTypesScreen.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/manager/DocumentTypesScreen.kt) - Scytales Manager UI
- [**OfferReviewScreen.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/openid4vci/OfferReviewScreen.kt) - OpenID4VCI UI
- [**OpenId4VciCoordinator.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/openid4vci/OpenId4VciCoordinator.kt) - Flow management
- [**SDK API Documentation**](../api/index.md) - Complete SDK reference

