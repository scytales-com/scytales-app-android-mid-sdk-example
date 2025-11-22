# Document Management

Manage digital credentials stored in the Scytales MID SDK wallet.

## Overview

The SDK provides comprehensive document management capabilities:

- **List Documents**: Retrieve all issued documents
- **View Details**: Access document metadata and claims
- **Delete Documents**: Remove documents from wallet
- **Filter Documents**: Query documents by type or format

All documents are stored securely using Android Keystore encryption and persist between app restarts.

## Document Types

The SDK manages different document types:

### IssuedDocument

Standard issued documents with claims:

```kotlin
interface IssuedDocument : Document {
    val id: String
    val docType: String
    val format: DocumentFormat
    val createdAt: Instant
    val usesStrongBox: Boolean
    val requiresUserAuth: Boolean
    val nameSpaces: Map<String, List<String>>
}
```

### Document Formats

| Format | Class | Description |
|--------|-------|-------------|
| **mso_mdoc** | `MsoMdocFormat` | ISO/IEC 18013-5 mobile documents |
| **SD-JWT-VC** | `SdJwtVcFormat` | Selective Disclosure JWT Verifiable Credentials |

---

## Listing Documents

### Get All Documents

Retrieve all documents in the wallet:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()
val documents = sdk.getDocuments()

documents.forEach { document ->
    if (document is IssuedDocument) {
        println("Document: ${document.id}")
        println("Type: ${document.docType}")
        println("Format: ${document.format}")
    }
}
```

### Filter Documents

Query specific document types:

```kotlin
// Get only IssuedDocuments
val issuedDocs = sdk.getDocuments { document ->
    document is IssuedDocument
}

// Filter by document type
val drivingLicenses = sdk.getDocuments { document ->
    document is IssuedDocument && 
    document.docType == "org.iso.18013.5.1.mDL"
}

// Filter by format
val msoMdocDocuments = sdk.getDocuments { document ->
    document is IssuedDocument && 
    document.format is MsoMdocFormat
}
```

### ViewModel Implementation

```kotlin
class HomeViewModel : ViewModel() {
    
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    fun loadDocuments() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            
            try {
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = HomeState.Error("SDK not initialized")
                    return@launch
                }
                
                val sdk = ScytalesSdkInitializer.getSdk()
                
                // Get all IssuedDocuments
                val documents = sdk.getDocuments { document ->
                    document is IssuedDocument
                }
                
                // Map to UI items
                val documentItems = documents
                    .filterIsInstance<IssuedDocument>()
                    .map { it.toDocumentItem() }
                
                _state.value = if (documentItems.isEmpty()) {
                    HomeState.Empty
                } else {
                    HomeState.Success(documentItems)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading documents", e)
                _state.value = HomeState.Error("Failed to load documents: ${e.message}")
            }
        }
    }
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/home/HomeViewModel.kt)

---

## Viewing Document Details

### Get Document by ID

Retrieve a specific document:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()
val documentId = "doc-123"

val document = sdk.getDocumentById(documentId) as? IssuedDocument

if (document != null) {
    println("Document found: ${document.docType}")
} else {
    println("Document not found")
}
```

### Access Document Metadata

```kotlin
val document: IssuedDocument = sdk.getDocumentById(documentId) as IssuedDocument

// Basic metadata
val id = document.id
val docType = document.docType
val createdAt = document.createdAt

// Format information
when (val format = document.format) {
    is MsoMdocFormat -> {
        println("Format: mso_mdoc")
        println("DocType: ${format.docType}")
    }
    is SdJwtVcFormat -> {
        println("Format: SD-JWT-VC")
        println("Type: ${format.type}")
        println("VCT: ${format.vct}")
    }
}

// Security properties
val usesStrongBox = document.usesStrongBox
val requiresUserAuth = document.requiresUserAuth

// Available namespaces
val nameSpaces = document.nameSpaces
```

### Access Document Claims

Retrieve document attributes (claims):

```kotlin
val document: IssuedDocument = sdk.getDocumentById(documentId) as IssuedDocument

// Get all claims grouped by namespace
val claimsByNamespace = document.nameSpaces

// Example: mDL document
claimsByNamespace.forEach { (namespace, claimNames) ->
    println("Namespace: $namespace")
    claimNames.forEach { claimName ->
        println("  - $claimName")
    }
}

// Output:
// Namespace: org.iso.18013.5.1
//   - given_name
//   - family_name
//   - birth_date
//   - issue_date
//   - expiry_date
//   - document_number
```

**Note**: For security reasons, the SDK does not expose raw claim values outside of presentation flows. Claims are only disclosed during verified presentations to authorized verifiers.

### ViewModel Implementation

```kotlin
class DocumentDetailsViewModel(
    private val documentId: String
) : ViewModel() {
    
    private val _state = MutableStateFlow<DocumentDetailsState>(DocumentDetailsState.Loading)
    val state: StateFlow<DocumentDetailsState> = _state.asStateFlow()
    
    init {
        loadDocumentDetails()
    }
    
    fun loadDocumentDetails() {
        viewModelScope.launch {
            _state.value = DocumentDetailsState.Loading
            
            try {
                if (!ScytalesSdkInitializer.isInitialized()) {
                    _state.value = DocumentDetailsState.Error("SDK not initialized")
                    return@launch
                }
                
                val sdk = ScytalesSdkInitializer.getSdk()
                val document = sdk.getDocumentById(documentId) as? IssuedDocument
                
                if (document == null) {
                    _state.value = DocumentDetailsState.Error("Document not found")
                    return@launch
                }
                
                val details = document.toDocumentDetails()
                _state.value = DocumentDetailsState.Success(details)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading document details", e)
                _state.value = DocumentDetailsState.Error("Failed to load document: ${e.message}")
            }
        }
    }
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/documentdetails/DocumentDetailsViewModel.kt)

---

## Deleting Documents

### Delete by ID

Remove a document from the wallet:

```kotlin
val sdk = ScytalesSdkInitializer.getSdk()
val documentId = "doc-123"

try {
    sdk.deleteDocumentById(documentId)
    println("Document deleted successfully")
} catch (e: Exception) {
    println("Failed to delete document: ${e.message}")
}
```

**Effects**:
- Document removed from storage
- Cryptographic keys deleted from Android Keystore
- Document cannot be recovered

### Delete in ViewModel

```kotlin
fun deleteDocument() {
    viewModelScope.launch {
        try {
            if (!ScytalesSdkInitializer.isInitialized()) {
                return@launch
            }
            
            val sdk = ScytalesSdkInitializer.getSdk()
            sdk.deleteDocumentById(documentId)
            
            Log.d(TAG, "Document deleted: $documentId")
            _state.value = DocumentDetailsState.Deleted
            
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting document", e)
            _state.value = DocumentDetailsState.Error("Failed to delete: ${e.message}")
        }
    }
}
```

### Confirmation Dialog

Always confirm deletion with user:

```kotlin
@Composable
fun DeleteConfirmationDialog(
    documentName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Document?") },
        text = { 
            Text("Are you sure you want to delete '$documentName'? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

---

## Document Storage

### Storage Location

Documents are stored in encrypted database:

```kotlin
// Configured during SDK initialization
val storageFile = File(context.noBackupFilesDir, "scytales_wallet.db")
configureDocumentManager(storageFile.absolutePath)
```

**Properties**:
- Location: `noBackupFilesDir` (excluded from cloud backups)
- Encryption: AES-256 with keys in Android Keystore
- Persistence: Survives app restarts
- Security: Protected by OS-level app sandboxing

### Key Management

Document keys are stored in Android Keystore:

```kotlin
// Configured during SDK initialization
configureDocumentKeyCreation(
    userAuthenticationRequired = false,
    userAuthenticationTimeout = Duration.INFINITE,
    useStrongBoxForKeys = true
)
```

**Properties**:
- `useStrongBoxForKeys`: Use hardware-backed keys when available
- `userAuthenticationRequired`: Require biometric/PIN for key usage
- Keys never leave secure hardware
- Automatic key deletion when document deleted

---

## Data Models

### DocumentItem (UI Model)

Convert `IssuedDocument` to UI-friendly model:

```kotlin
data class DocumentItem(
    val id: String,
    val name: String,
    val docType: String,
    val format: String,
    val createdAt: Instant,
    val credentialCount: Int
)

suspend fun IssuedDocument.toDocumentItem(): DocumentItem {
    val (formatName, docTypeName) = when (val fmt = format) {
        is MsoMdocFormat -> "mso_mdoc" to fmt.docType
        is SdJwtVcFormat -> "sd-jwt-vc" to (fmt.vct ?: fmt.type)
        else -> "unknown" to "unknown"
    }
    
    val displayName = docTypeName.split(".").lastOrNull() ?: docTypeName
    val claimCount = nameSpaces.values.sumOf { it.size }
    
    return DocumentItem(
        id = id,
        name = displayName,
        docType = docTypeName,
        format = formatName,
        createdAt = createdAt,
        credentialCount = claimCount
    )
}
```

### DocumentDetails (UI Model)

Detailed document information:

```kotlin
data class DocumentDetails(
    val id: String,
    val name: String,
    val docType: String,
    val format: String,
    val createdAt: Instant,
    val usesStrongBox: Boolean,
    val requiresUserAuth: Boolean,
    val claimsByNamespace: Map<String, List<String>>
)

suspend fun IssuedDocument.toDocumentDetails(): DocumentDetails {
    val (formatName, docTypeName) = when (val fmt = format) {
        is MsoMdocFormat -> "mso_mdoc" to fmt.docType
        is SdJwtVcFormat -> "sd-jwt-vc" to (fmt.vct ?: fmt.type)
        else -> "unknown" to "unknown"
    }
    
    return DocumentDetails(
        id = id,
        name = docTypeName.split(".").lastOrNull() ?: docTypeName,
        docType = docTypeName,
        format = formatName,
        createdAt = createdAt,
        usesStrongBox = usesStrongBox,
        requiresUserAuth = requiresUserAuth,
        claimsByNamespace = nameSpaces
    )
}
```

---

## UI State Management

### Home Screen State

```kotlin
sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val documents: List<DocumentItem>) : HomeState()
    data object Empty : HomeState()
    data class Error(val message: String) : HomeState()
}
```

### Document Details State

```kotlin
sealed class DocumentDetailsState {
    data object Loading : DocumentDetailsState()
    data class Success(val details: DocumentDetails) : DocumentDetailsState()
    data object Deleted : DocumentDetailsState()
    data class Error(val message: String) : DocumentDetailsState()
}
```

---

## Best Practices

### 1. Refresh After Changes

Reload document list after issuance or deletion:

```kotlin
// After document issued
onDocumentIssued = {
    homeViewModel.loadDocuments()
}

// After document deleted
onDocumentDeleted = {
    homeViewModel.loadDocuments()
}
```

### 2. Handle Empty State

Show appropriate UI when no documents:

```kotlin
when (state) {
    is HomeState.Empty -> {
        EmptyStateScreen(
            message = "No documents in wallet",
            action = { navigateToIssuance() }
        )
    }
}
```

### 3. Confirm Destructive Actions

Always confirm before deleting:

```kotlin
var showDeleteDialog by remember { mutableStateOf(false) }

if (showDeleteDialog) {
    DeleteConfirmationDialog(
        documentName = document.name,
        onConfirm = {
            viewModel.deleteDocument()
            showDeleteDialog = false
        },
        onDismiss = { showDeleteDialog = false }
    )
}
```

### 4. Handle Errors Gracefully

Show user-friendly error messages:

```kotlin
when (state) {
    is HomeState.Error -> {
        ErrorScreen(
            message = state.message,
            onRetry = { viewModel.loadDocuments() }
        )
    }
}
```

### 5. Use Loading States

Show progress during operations:

```kotlin
when (state) {
    is HomeState.Loading -> {
        LoadingIndicator(message = "Loading documents...")
    }
}
```

---

## Security Considerations

### Document Access

- Documents only accessible within app sandbox
- Cryptographic keys protected by Android Keystore
- StrongBox hardware security when available

### Data Privacy

- Claim values not exposed in raw form
- Selective disclosure during presentations
- User consent required for all presentations

### Deletion

- Immediate removal from storage
- Cryptographic keys deleted
- No recovery possible

---

## Common Use Cases

### Display Document List

```kotlin
@Composable
fun DocumentListScreen(
    documents: List<DocumentItem>,
    onDocumentClick: (DocumentItem) -> Unit
) {
    LazyColumn {
        items(documents, key = { it.id }) { document ->
            DocumentCard(
                document = document,
                onClick = { onDocumentClick(document) }
            )
        }
    }
}
```

### Show Document Details

```kotlin
@Composable
fun DocumentDetailsScreen(
    details: DocumentDetails,
    onDelete: () -> Unit
) {
    Column {
        Text(text = details.name, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Type: ${details.docType}")
        Text(text = "Format: ${details.format}")
        Text(text = "Created: ${details.createdAt}")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Claims", style = MaterialTheme.typography.titleMedium)
        details.claimsByNamespace.forEach { (namespace, claims) ->
            Text(text = namespace, style = MaterialTheme.typography.titleSmall)
            claims.forEach { claim ->
                Text(text = "  • $claim")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onDelete) {
            Text("Delete Document")
        }
    }
}
```

### Refresh Documents

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    PullToRefreshBox(
        isRefreshing = state is HomeState.Loading,
        onRefresh = { viewModel.loadDocuments() }
    ) {
        when (val currentState = state) {
            is HomeState.Success -> {
                DocumentListScreen(currentState.documents)
            }
            is HomeState.Empty -> {
                EmptyStateScreen()
            }
            is HomeState.Error -> {
                ErrorScreen(currentState.message)
            }
        }
    }
}
```

---

## Next Steps

- [**Document Issuance**](document-issuance.md) - Issue new credentials
- [**Document Presentation**](document-presentation.md) - Present credentials to verifiers

## Reference

- [**HomeScreen.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/home/HomeScreen.kt) - Document list implementation
- [**DocumentDetailsScreen.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/documentdetails/DocumentDetailsScreen.kt) - Document details implementation
- [**HomeViewModel.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/home/HomeViewModel.kt) - Document list logic
- [**DocumentDetailsViewModel.kt**](../../app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/documentdetails/DocumentDetailsViewModel.kt) - Document details logic
- [**SDK API Documentation**](../api/index.md) - Complete SDK reference

