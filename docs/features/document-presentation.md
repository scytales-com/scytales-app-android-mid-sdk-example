# Document Presentation

Present digital credentials to verifiers using the Scytales MID SDK.

## Overview

The SDK supports three methods for presenting credentials:

1. **Proximity Presentation**: In-person verification using Bluetooth Low Energy (BLE) with QR code engagement
2. **Remote Presentation**: Online verification using OpenID for Verifiable Presentations (OpenID4VP)
3. **DCAPI (Digital Credentials API)**: Browser-initiated credential requests following W3C standards

All methods follow ISO 18013-5 standards for secure credential presentation with user consent.

## Presentation Methods Comparison

| Feature | Proximity (BLE) | Remote (OpenID4VP) | DCAPI (Browser) |
|---------|----------------|--------------------|--------------------|
| **Initiation** | User generates QR code | User scans QR code | Browser triggers intent |
| **Protocol** | ISO 18013-5 over BLE | OpenID4VP over HTTPS | ISO/IEC TS 18013-7:2025 (org-iso-mdoc) |
| **Use Case** | In-person verification | Online verification | Website login/verification |
| **Engagement** | QR code + BLE | Deep link + HTTPS | System intent |
| **User Action** | Show QR, accept on device | Scan QR, approve | Approve browser request |

---

## Method 1: Proximity Presentation (BLE)

Present credentials in person to a verifier using Bluetooth Low Energy.

### Prerequisites

- Bluetooth permissions granted
- Proximity presentation configured in SDK initialization
- Issued documents in wallet

### User Flow

1. User navigates to proximity presentation screen
2. App generates QR code for device engagement
3. Verifier scans QR code
4. BLE connection established automatically
5. Verifier sends document request
6. User reviews and approves/denies request
7. Documents transmitted over BLE
8. Verification complete

### Permissions Required

Request Bluetooth permissions before starting:

```kotlin
// Android 12+
val permissions = listOf(
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_ADVERTISE
)

// Android 11 and below
val permissions = listOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_FINE_LOCATION
)
```

### Implementation

#### Step 1: Attach Transfer Event Listener

Listen for transfer events from the SDK:

```kotlin
val listener = TransferEvent.Listener { event ->
    when (event) {
        is TransferEvent.QrEngagementReady -> {
            // Display QR code to user
            val qrBitmap = event.qrCode.asBitmap(size = 800)
        }
        
        is TransferEvent.Connecting -> {
            // Show connecting state
        }
        
        is TransferEvent.Connected -> {
            // BLE connection established
        }
        
        is TransferEvent.RequestReceived -> {
            // Show request to user for approval
            val requestedDocuments = event.requestedDocuments
        }
        
        is TransferEvent.ResponseSent -> {
            // Response sent successfully
        }
        
        is TransferEvent.Redirect -> {
            // Verifier provided redirect URL (optional)
        }
        
        is TransferEvent.Disconnected -> {
            // Connection closed
        }
        
        is TransferEvent.Error -> {
            // Handle error
        }
    }
}

sdk.addTransferEventListener(listener)
```

#### Step 2: Start Proximity Presentation

Start the presentation to generate QR code:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()
sdk.startProximityPresentation()
```

**Result**: SDK generates QR code and emits `TransferEvent.QrEngagementReady` event.

#### Step 3: Display QR Code

Display the QR code for the verifier to scan:

```kotlin
when (event) {
    is TransferEvent.QrEngagementReady -> {
        val qrBitmap = event.qrCode.asBitmap(size = 800)
        // Display bitmap in UI
    }
}
```

#### Step 4: Handle Request

When verifier requests documents, process and show to user:

```kotlin
when (event) {
    is TransferEvent.RequestReceived -> {
        // Process the request
        val processedRequest = event.processRequest(
            sdk.getDocuments()
        ).getOrThrow()
        
        when (processedRequest) {
            is RequestProcessor.ProcessedRequest.Success -> {
                // Show requested documents to user
                val requestedDocs = processedRequest.requestedDocuments
                showApprovalDialog(requestedDocs, processedRequest)
            }
            
            is RequestProcessor.ProcessedRequest.Failure -> {
                // Request cannot be fulfilled
                val error = processedRequest.error
            }
        }
    }
}
```

#### Step 5: Send Response

After user approval, generate and send response:

```kotlin
fun approveRequest(
    processedRequest: RequestProcessor.ProcessedRequest.Success,
    requestedDocuments: List<RequestedDocument>
) {
    // Disclose all requested items
    val disclosedDocuments = DisclosedDocuments(
        requestedDocuments.map { doc ->
            DisclosedDocument(
                documentId = doc.documentId,
                disclosedItems = doc.requestedItems.keys.toList()
            )
        }
    )
    
    // Generate response
    val response = processedRequest
        .generateResponse(disclosedDocuments)
        .getOrThrow()
    
    // Send to verifier
    sdk.sendResponse(response)
}
```

#### Step 6: Stop Presentation

Clean up when done:

```kotlin
sdk.stopProximityPresentation()
sdk.removeAllTransferEventListeners()
```

### Complete ViewModel Example

```kotlin
class ProximityPresentationViewModel : ViewModel() {
    
    private val _state = MutableStateFlow<ProximityState>(ProximityState.Idle)
    val state: StateFlow<ProximityState> = _state.asStateFlow()
    
    private val sdk by lazy { ScytalesSdkInitializer.getSdk() }
    private var currentProcessedRequest: RequestProcessor.ProcessedRequest.Success? = null
    
    fun startProximityPresentation() {
        _state.value = ProximityState.Initializing
        
        attachTransferEventListener()
        
        viewModelScope.launch {
            sdk.startProximityPresentation()
        }
    }
    
    fun approveRequest(requestedDocuments: List<RequestedDocument>) {
        val processedRequest = currentProcessedRequest ?: return
        _state.value = ProximityState.SendingResponse
        
        viewModelScope.launch {
            val disclosedDocuments = DisclosedDocuments(
                requestedDocuments.map {
                    DisclosedDocument(it.documentId, it.requestedItems.keys.toList())
                }
            )
            val response = processedRequest.generateResponse(disclosedDocuments).getOrThrow()
            sdk.sendResponse(response)
        }
    }
    
    fun stopProximityPresentation() {
        viewModelScope.launch {
            sdk.stopProximityPresentation()
            sdk.removeAllTransferEventListeners()
            _state.value = ProximityState.Idle
        }
    }
    
    private fun attachTransferEventListener() {
        sdk.addTransferEventListener(TransferEvent.Listener { event ->
            when (event) {
                is TransferEvent.QrEngagementReady -> {
                    _state.value = ProximityState.QrReady(event.qrCode.asBitmap(800))
                }
                is TransferEvent.Connected -> {
                    _state.value = ProximityState.Connected
                }
                is TransferEvent.RequestReceived -> {
                    handleRequestReceived(event)
                }
                is TransferEvent.ResponseSent -> {
                    _state.value = ProximityState.Success
                }
                is TransferEvent.Error -> {
                    _state.value = ProximityState.Error(event.error.message)
                }
                // ... handle other events
            }
        })
    }
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/proximity/ProximityPresentationViewModel.kt)

### Transfer Events

| Event | Description | Action Required |
|-------|-------------|-----------------|
| `QrEngagementReady` | QR code generated | Display QR to user |
| `Connecting` | BLE connection starting | Show connecting UI |
| `Connected` | BLE connected | Show connected status |
| `RequestReceived` | Verifier request received | Process and show to user |
| `ResponseSent` | Response transmitted | Show success |
| `Redirect` | Verifier redirect URL available | Open URL (optional) |
| `Disconnected` | Connection closed | Return to idle |
| `Error` | Error occurred | Show error message |

### Error Handling

Common errors:

| Error | Cause | Solution |
|-------|-------|----------|
| `BluetoothDisabled` | Bluetooth turned off | Prompt user to enable |
| `PermissionDenied` | Missing permissions | Request permissions |
| `ConnectionFailed` | BLE connection lost | Retry connection |
| `NoMatchingDocuments` | Wallet has no requested docs | Inform user |

---

## Method 2: Remote Presentation (OpenID4VP)

Present credentials online to a verifier using OpenID for Verifiable Presentations.

### Prerequisites

- Remote presentation configured in SDK initialization
- OpenID4VP request URI (from QR code or deep link)
- Issued documents in wallet

### User Flow

1. User scans verifier's QR code containing OpenID4VP request
2. App receives deep link and starts remote presentation
3. SDK fetches presentation request from verifier
4. User reviews requested documents
5. User approves/denies request
6. SDK sends response to verifier via HTTPS
7. Verification complete

### Request URI Format

OpenID4VP requests are URIs:

```
mdoc-openid4vp://
  ?client_id=https://verifier.example.com
  &request_uri=https://verifier.example.com/request/abc123
```

### Implementation

#### Step 1: Handle Deep Link

Configure deep link handling in `MainActivity`:

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    
    intent.data?.let { uri ->
        if (uri.scheme == "mdoc-openid4vp") {
            // Navigate to remote presentation screen
            navigateToRemotePresentation(uri)
        }
    }
}
```

#### Step 2: Start Remote Presentation

Start presentation with the request URI:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()

// Attach listener first
sdk.addTransferEventListener(TransferEvent.Listener { event ->
    handleTransferEvent(event)
})

// Start remote presentation
sdk.startRemotePresentation(requestUri)
```

**Process**:
1. SDK fetches presentation definition from verifier
2. Matches requested documents in wallet
3. Emits `TransferEvent.RequestReceived` event

#### Step 3: Handle Request

Same as proximity presentation:

```kotlin
when (event) {
    is TransferEvent.RequestReceived -> {
        val processedRequest = event.processRequest(
            sdk.getDocuments()
        ).getOrThrow()
        
        when (processedRequest) {
            is RequestProcessor.ProcessedRequest.Success -> {
                showApprovalDialog(processedRequest.requestedDocuments)
            }
            is RequestProcessor.ProcessedRequest.Failure -> {
                showError(processedRequest.error)
            }
        }
    }
}
```

#### Step 4: Send Response

Generate and send response via HTTPS:

```kotlin
fun approveRequest(
    processedRequest: RequestProcessor.ProcessedRequest.Success,
    requestedDocuments: List<RequestedDocument>
) {
    val disclosedDocuments = DisclosedDocuments(
        requestedDocuments.map {
            DisclosedDocument(it.documentId, it.requestedItems.keys.toList())
        }
    )
    
    val response = processedRequest.generateResponse(disclosedDocuments).getOrThrow()
    sdk.sendResponse(response)
}
```

#### Step 5: Stop Presentation

Clean up:

```kotlin
sdk.stopRemotePresentation()
sdk.removeAllTransferEventListeners()
```

### Complete Example

```kotlin
class RemotePresentationViewModel : ViewModel() {
    
    private val _state = MutableStateFlow<RemoteState>(RemoteState.Idle)
    val state: StateFlow<RemoteState> = _state.asStateFlow()
    
    fun startRemotePresentation(uri: Uri) {
        _state.value = RemoteState.ProcessingRequest
        
        attachTransferEventListener()
        
        viewModelScope.launch {
            sdk.startRemotePresentation(uri)
        }
    }
    
    private fun attachTransferEventListener() {
        sdk.addTransferEventListener(TransferEvent.Listener { event ->
            when (event) {
                is TransferEvent.RequestReceived -> {
                    handleRequestReceived(event)
                }
                is TransferEvent.ResponseSent -> {
                    _state.value = RemoteState.Success
                }
                is TransferEvent.Error -> {
                    _state.value = RemoteState.Error(event.error.message)
                }
                // ... other events
            }
        })
    }
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/remote/RemotePresentationViewModel.kt)

### Deep Link Configuration

Add to `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    
    <data android:scheme="mdoc-openid4vp" android:host="*" />
</intent-filter>
```

---

## Method 3: DCAPI (Digital Credentials API)

Handle browser-initiated credential requests using the W3C Digital Credentials API.

**Protocol**: The DCAPI implementation follows the `org-iso-mdoc` protocol according to [ISO/IEC TS 18013-7:2025](https://www.iso.org/standard/91154.html) Annex C.

**Note**: DCAPI is disabled by default and must be explicitly enabled in SDK configuration.

### Prerequisites

- DCAPI configured in SDK initialization (explicitly enabled)
- Intent filters configured for DCAPI actions
- Issued documents in wallet

### User Flow

1. User visits website that requests credentials
2. Browser triggers system credential picker
3. User selects wallet app
4. App receives `GET_CREDENTIAL` intent
5. SDK processes request automatically
6. User reviews and approves/denies
7. App returns result intent to browser
8. Website receives credentials

### Implementation

#### Step 1: Configure Intent Filters

Add to `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="androidx.credentials.registry.provider.action.GET_CREDENTIAL" />
    <action android:name="androidx.identitycredentials.action.GET_CREDENTIALS" />
    <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

#### Step 2: Handle DCAPI Intent

Start DCAPI presentation with the intent:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    if (intent.action == "androidx.credentials.registry.provider.action.GET_CREDENTIAL") {
        startDCAPIPresentation(intent)
    }
}

fun startDCAPIPresentation(intent: Intent) {
    val sdk = ScytalesSdkInitializer.getSdk()
    
    // Attach listener
    sdk.addTransferEventListener(TransferEvent.Listener { event ->
        handleTransferEvent(event)
    })
    
    // Start DCAPI presentation
    sdk.startDCAPIPresentation(intent)
}
```

#### Step 3: Handle Request

Same as other methods:

```kotlin
when (event) {
    is TransferEvent.RequestReceived -> {
        val processedRequest = event.processRequest(sdk.getDocuments()).getOrThrow()
        
        when (processedRequest) {
            is RequestProcessor.ProcessedRequest.Success -> {
                showApprovalDialog(processedRequest.requestedDocuments)
            }
        }
    }
}
```

#### Step 4: Return Result to Browser

After sending response, finish activity with result:

```kotlin
when (event) {
    is TransferEvent.ResponseSent -> {
        // DCAPI-specific: return intent to browser
        val resultIntent = Intent().apply {
            putExtra("response", event.response)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
    
    is TransferEvent.Error -> {
        // Return error to browser
        setResult(Activity.RESULT_CANCELED, null)
        finish()
    }
}
```

### Complete Example

```kotlin
class DCAPIPresentationViewModel : ViewModel() {
    
    private val _state = MutableStateFlow<DCAPIState>(DCAPIState.ProcessingIntent)
    val state: StateFlow<DCAPIState> = _state.asStateFlow()
    
    fun startDCAPIPresentation(intent: Intent) {
        _state.value = DCAPIState.ProcessingIntent
        
        attachTransferEventListener()
        
        viewModelScope.launch {
            sdk.startDCAPIPresentation(intent)
        }
    }
    
    private fun attachTransferEventListener() {
        sdk.addTransferEventListener(TransferEvent.Listener { event ->
            when (event) {
                is TransferEvent.RequestReceived -> {
                    handleRequestReceived(event)
                }
                is TransferEvent.ResponseSent -> {
                    _state.value = DCAPIState.ResponseReady(createResultIntent(event))
                }
                is TransferEvent.Error -> {
                    _state.value = DCAPIState.Error(event.error.message, null)
                }
            }
        })
    }
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/dcapi/DCAPIPresentationViewModel.kt)

---

## Common Implementation Patterns

### Requested Documents

All methods receive `RequestedDocument` objects:

```kotlin
data class RequestedDocument(
    val documentId: String,
    val docType: String,
    val requestedItems: Map<String, Boolean> // nameSpace -> required
)
```

### Disclosed Documents

Control what to disclose:

```kotlin
// Disclose all requested items
val disclosedDocuments = DisclosedDocuments(
    requestedDocuments.map { doc ->
        DisclosedDocument(
            documentId = doc.documentId,
            disclosedItems = doc.requestedItems.keys.toList()
        )
    }
)

// Selective disclosure
val disclosedDocuments = DisclosedDocuments(
    listOf(
        DisclosedDocument(
            documentId = "doc-123",
            disclosedItems = listOf("given_name", "family_name") // Only name
        )
    )
)
```

### Error Handling

Common pattern for all methods:

```kotlin
when (event) {
    is TransferEvent.Error -> {
        when (event.error) {
            is TransferError.NoMatchingDocuments -> {
                showError("You don't have the required documents")
            }
            is TransferError.InvalidRequest -> {
                showError("Invalid request from verifier")
            }
            is TransferError.NetworkError -> {
                showError("Network error, please try again")
            }
            else -> {
                showError("Presentation failed: ${event.error.message}")
            }
        }
    }
}
```

## Security Considerations

### User Consent

- Always show requested documents to user before sending
- Clearly display what information will be shared
- Allow user to deny requests

### Selective Disclosure

- Only disclose required items when possible
- Let user choose what to share (optional items)
- Never send more data than requested

### Connection Security

- Proximity: Encrypted BLE channel
- Remote: HTTPS with certificate validation
- DCAPI: System-mediated secure channel

## Next Steps

- [**Document Management**](document-management.md) - View and manage issued documents
- [**Document Issuance**](document-issuance.md) - Issue new credentials

## Reference

- [**ProximityPresentationScreen.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/proximity/ProximityPresentationScreen.kt) - Proximity UI implementation
- [**RemotePresentationScreen.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/remote/RemotePresentationScreen.kt) - Remote UI implementation
- [**DCAPIPresentationScreen.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/dcapi/DCAPIPresentationScreen.kt) - DCAPI UI implementation
- [**SDK API Documentation**](../api/index.md) - Complete SDK reference

