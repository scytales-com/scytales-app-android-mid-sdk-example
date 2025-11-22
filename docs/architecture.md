# Architecture Guide

[Back to Documentation](README.md)

Comprehensive guide to the application architecture, design patterns, and implementation strategies used in the Scytales MID SDK Android Example application.

## Overview

This reference Android application demonstrates best practices for integrating the Scytales MID SDK. The architecture emphasizes clarity, maintainability, and production-ready patterns while showcasing all SDK capabilities.

**Key Characteristics:**
- **UI Framework**: Jetpack Compose with Material Design 3
- **State Management**: Sealed classes for UI state with ViewModel pattern
- **Navigation**: Sealed class-based custom navigation with Material3 Adaptive Navigation Suite
- **SDK Integration**: Thread-safe singleton initialization with coroutine support
- **Language**: Kotlin 2.2.21 with coroutines for async operations
- **Min SDK**: API 28 (Android 9.0)
- **Target SDK**: API 36

## Application Structure

```
app/src/main/java/com/scytales/mid/sdk/example/app/
├── MainActivity.kt              # Main entry point, navigation coordinator
├── ScytalesApp.kt              # Application class, SDK initialization
├── sdk/                        # SDK configuration and initialization
│   ├── ScytalesSdkInitializer.kt
│   ├── SdkConfig.kt
│   ├── SdkState.kt
│   └── error/
│       └── SdkInitializationError.kt
├── ui/                         # User interface layer
│   ├── screens/                # Feature screens
│   │   ├── home/              # Document list and main navigation
│   │   ├── manager/           # Scytales Manager issuance
│   │   ├── openid4vci/        # OpenID4VCI credential issuance
│   │   ├── proximity/         # BLE proximity presentation
│   │   ├── remote/            # OpenID4VP remote presentation
│   │   ├── dcapi/             # Digital Credentials API (browser)
│   │   ├── documentdetails/   # Document viewer
│   │   └── common/            # Shared UI components
│   ├── components/            # Reusable UI components
│   │   ├── DocumentCard.kt
│   │   ├── DocumentTypeCard.kt
│   │   └── IssuanceMethodDialog.kt
│   └── theme/                 # Material3 theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── openid4vci/                # OpenID4VCI coordinator logic
    └── OpenId4VciCoordinator.kt
```

## Architecture Patterns

### 1. SDK Integration Layer

The SDK integration is handled through a dedicated layer that provides thread-safe, lifecycle-aware access to the Scytales MID SDK.

#### ScytalesSdkInitializer

**Purpose**: Singleton object managing SDK initialization and providing global access.

**Key Features:**
- Thread-safe initialization using Mutex
- State tracking (NotInitialized, Initializing, Initialized, Error)
- Validation of configuration before initialization
- Comprehensive error handling with specific error types

**Usage:**
```kotlin
// In Application class
class ScytalesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        runBlocking {
            ScytalesSdkInitializer.initialize(this@ScytalesApp)
                .onSuccess { Log.d(TAG, "SDK initialized") }
                .onFailure { error -> Log.e(TAG, "SDK init failed", error) }
        }
    }
}

// Anywhere in the app
val sdk = ScytalesSdkInitializer.getSdk()
val documents = sdk.getDocuments()
```

#### SdkConfig

**Purpose**: Centralized configuration object for SDK settings.

**Configuration Options:**
- `licenseKey`: Scytales SDK license key (mandatory)
- `organizationUrl`: Organization base URL for Scytales Manager API (mandatory)
- `faceTecDeviceKey`: FaceTec device key for biometric enrollment (optional)
- `faceTecPublicKey`: FaceTec public encryption key for biometric enrollment (optional)
- `isFaceTecConfigured`: Computed property indicating if FaceTec is configured

**Example:**
```kotlin
object SdkConfig {
    const val licenseKey: String = "your-license-key"
    const val organizationUrl: String = "https://your-scytales-manager.url"
    val faceTecDeviceKey: String? = null
    val faceTecPublicKey: String? = null
    
    val isFaceTecConfigured: Boolean
        get() = !faceTecDeviceKey.isNullOrBlank() && !faceTecPublicKey.isNullOrBlank()
}
```

#### SdkState

**Purpose**: Represents the current state of SDK initialization.

**States:**
- `NotInitialized`: SDK has not been initialized yet
- `Initializing`: SDK initialization in progress
- `Initialized(sdk)`: SDK successfully initialized with instance
- `Failed(error)`: SDK initialization failed with error

### 2. Presentation Layer (UI)

The UI layer follows a composable-first approach with ViewModels for state management.

#### Screen Architecture

Each feature screen follows this pattern:

```
Screen/
├── [Feature]Screen.kt          # Composable UI
├── [Feature]ViewModel.kt       # State management
├── [Feature]State.kt           # UI state data class
└── [Feature]Event.kt           # User events (optional)
```

**Example: HomeScreen**

```kotlin
// HomeState.kt
sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val documents: List<DocumentItem>) : HomeState()
    data object Empty : HomeState()
    data class Error(val message: String) : HomeState()
}

data class DocumentItem(
    val id: String,
    val name: String,
    val docType: String,
    val format: String,
    val createdAt: Instant,
    val credentialCount: Int
)

// HomeViewModel.kt
class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    init {
        loadDocuments()
    }
    
    fun loadDocuments() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            try {
                val sdk = ScytalesSdkInitializer.getSdk()
                val documents = sdk.getDocuments()
                    .map { it.toDocumentItem() }
                
                _state.value = if (documents.isEmpty()) {
                    HomeState.Empty
                } else {
                    HomeState.Success(documents)
                }
            } catch (e: Exception) {
                _state.value = HomeState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// HomeScreen.kt
@Composable
fun HomeScreen(
    onSelectScytalesManager: () -> Unit,
    onSelectOpenId4Vci: () -> Unit,
    onDocumentClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Show issuance dialog */ }) {
                Icon(Icons.Default.Add, "Add Document")
            }
        }
    ) { padding ->
        when (val currentState = state) {
            is HomeState.Loading -> LoadingIndicator()
            is HomeState.Error -> ErrorMessage(currentState.message)
            is HomeState.Empty -> EmptyState()
            is HomeState.Success -> DocumentList(
                documents = currentState.documents,
                onDocumentClick = onDocumentClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
```

### 3. Navigation Architecture

Custom navigation system using sealed classes and composable state.

#### Screen Definitions

```kotlin
sealed class Screen {
    data object Home : Screen()
    data object DocumentTypes : Screen()
    data object QRScanner : Screen()
    data class OfferReview(
        val offerUri: String? = null,
        val authorizationUri: android.net.Uri? = null
    ) : Screen()
    data class DocumentDetails(val documentId: String) : Screen()
    data object ProximityPresentation : Screen()
    data object RemoteQRScanner : Screen()
    data class RemotePresentation(val requestUri: String) : Screen()
    data class DCAPIPresentation(val intent: Intent) : Screen()
}
```

#### Navigation Destinations (Bottom Nav)

```kotlin
enum class AppDestinations(
    val label: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Default.Home),
    SHARE("Share", Icons.Outlined.Share),
    VERIFY("Verify", Icons.Default.QrCodeScanner)
}
```

#### Navigation Flow

```kotlin
@Composable
fun ScytalesappandroidmidsdkexampleApp(activity: ComponentActivity) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    
    when (currentScreen) {
        Screen.Home -> {
            NavigationSuiteScaffold(
                navigationSuiteItems = { /* Bottom nav items */ }
            ) {
                when (currentDestination) {
                    AppDestinations.HOME -> HomeScreen(/* ... */)
                    AppDestinations.SHARE -> { currentScreen = Screen.ProximityPresentation }
                    AppDestinations.VERIFY -> { currentScreen = Screen.RemoteQRScanner }
                }
            }
        }
        is Screen.DocumentDetails -> DocumentDetailsScreen(/* ... */)
        is Screen.RemotePresentation -> RemotePresentationScreen(/* ... */)
        // ... other screens
    }
}
```

### 4. Deep Link Handling

The application handles multiple deep link schemes for different credential flows.

#### Supported Schemes

| Scheme | Purpose | Handler |
|--------|---------|---------|
| `eudi-openid4ci://authorize` | OpenID4VCI authorization callback | MainActivity → OfferReview |
| `mdoc-openid4vp://` | OpenID4VP presentation request | MainActivity → RemotePresentation |
| `androidx.credentials.registry.provider.action.GET_CREDENTIAL` | DCAPI request (Android 14+) | MainActivity → DCAPIPresentation |

#### Implementation

```kotlin
class MainActivity : ComponentActivity() {
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        intent.data?.let { uri ->
            when {
                uri.scheme == "eudi-openid4ci" && uri.host == "authorize" -> {
                    // OpenID4VCI authorization redirect
                    // Navigate to OfferReview screen
                }
                uri.scheme == "mdoc-openid4vp" -> {
                    // OpenID4VP remote presentation request
                    // Navigate to RemotePresentation screen
                }
            }
        }
        
        if (intent.action == "androidx.credentials.registry.provider.action.GET_CREDENTIAL") {
            // DCAPI request from browser
            // Navigate to DCAPIPresentation screen
        }
    }
}
```

## Feature Implementation Patterns

### Document Issuance

#### Scytales Manager Issuance

**Flow:**
1. User selects "Scytales Manager" from issuance options
2. Navigate to DocumentTypesScreen
3. Fetch available document types from SDK
4. User selects document type
5. SDK handles biometric enrollment (FaceTec) and issuance
6. Navigate back to home with new document

**Implementation:**
```kotlin
// DocumentTypesScreen.kt
@Composable
fun DocumentTypesScreen(
    activity: ComponentActivity,
    onNavigateBack: () -> Unit,
    onDocumentIssued: () -> Unit
) {
    val sdk = ScytalesSdkInitializer.getSdk()
    var availableTypes by remember { mutableStateOf<List<AvailableDocumentType>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        availableTypes = sdk.getAvailableDocumentTypes()
    }
    
    LazyColumn {
        items(availableTypes) { docType ->
            DocumentTypeCard(
                documentType = docType,
                onClick = {
                    // Create signup manager and issue document
                    val signupManager = sdk.createSignManager()
                    signupManager.issueDocument(
                        activity = activity,
                        docType = docType.template,
                        format = docType.format
                    )
                    onDocumentIssued()
                }
            )
        }
    }
}
```

#### OpenID4VCI Issuance

**Flow:**
1. User scans QR code containing credential offer
2. Parse offer and display details
3. User confirms and initiates issuance
4. Handle authorization code flow (redirect to browser if needed)
5. SDK processes credential issuance
6. Display success/error and return to home

**Implementation:**

The OpenID4VCI flow is handled by the OpenId4VciCoordinator class, which manages the credential offer parsing, authorization flow, and credential issuance. The implementation handles both pre-authorized and authorization code flows, with automatic browser redirection when required.

### Document Presentation

#### Proximity Presentation (BLE)

**Flow:**
1. User selects "Share" tab
2. Display QR code for verifier to scan (device engagement)
3. Establish BLE connection
4. Receive request from verifier
5. User selects which claims to share
6. SDK transmits selected data
7. Show success/error result

#### Remote Presentation (OpenID4VP)

**Flow:**
1. User scans verifier's QR code or opens deep link
2. Parse presentation request
3. Validate verifier (client ID scheme)
4. User selects document and claims to share
5. SDK generates VP token
6. SDK sends response to verifier's redirect URI
7. Show success/error result

#### DCAPI Presentation (Browser)

**Flow:**
1. User visits website requesting credentials
2. Browser shows system credential picker
3. User selects this app
4. MainActivity receives DCAPI intent
5. Parse request and display to user
6. User confirms sharing
7. SDK generates response
8. Return credential to browser via Activity result

## State Management Strategy

### ViewModel Pattern

Each screen has a dedicated ViewModel that:
- Holds UI state in `StateFlow<State>`
- Exposes functions for user actions
- Interacts with SDK through coroutines
- Survives configuration changes

### State Types

```kotlin
// Loading state
data class ScreenState(
    val isLoading: Boolean = false,
    val data: Data? = null,
    val error: String? = null
)

// Success/Error states
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### Event Handling

```kotlin
// Events from UI
sealed class ScreenEvent {
    object LoadData : ScreenEvent()
    object Refresh : ScreenEvent()
    data class ItemClicked(val id: String) : ScreenEvent()
}

// ViewModel handling
fun onEvent(event: ScreenEvent) {
    when (event) {
        is ScreenEvent.LoadData -> loadData()
        is ScreenEvent.Refresh -> refresh()
        is ScreenEvent.ItemClicked -> handleItemClick(event.id)
    }
}
```

## Error Handling

### SDK Error Handling

```kotlin
try {
    val sdk = ScytalesSdkInitializer.getSdk()
    val result = sdk.someOperation()
    // Handle success
} catch (e: NotInitializedException) {
    // SDK not initialized
} catch (e: UnsupportedFeatureException) {
    // Feature not enabled in license
} catch (e: Exception) {
    // Generic error
}
```

### User-Facing Errors

```kotlin
@Composable
fun ErrorDisplay(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = error, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
```

## Threading and Coroutines

### Main Thread Operations
- All Compose UI rendering
- StateFlow updates
- Navigation changes

### Background Operations
- SDK initialization
- Network requests
- Document operations
- Cryptographic operations

### Pattern
```kotlin
class ScreenViewModel : ViewModel() {
    fun performOperation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val result = withContext(Dispatchers.IO) {
                // Heavy operation on background thread
                sdk.performOperation()
            }
            
            // Update UI on main thread
            _state.update { it.copy(isLoading = false, data = result) }
        }
    }
}
```

## Testing Strategy

This example application focuses on demonstrating SDK integration patterns. For production applications, consider implementing:

### Unit Testing
- ViewModel state transitions
- SDK initialization logic
- Error handling paths
- Business logic validation

### Integration Testing
- SDK operations end-to-end
- Navigation flows
- Deep link handling
- Document lifecycle operations

### UI Testing (Compose)
- Screen rendering with different states
- User interaction flows
- State changes and updates
- Error state handling

## Security Considerations

### SDK Initialization
- License key stored in BuildConfig (not in source control)
- Organization ID configured externally
- Secure storage location for document database

### Document Storage
- Android Keystore for cryptographic keys
- Encrypted storage via SDK
- Secure deletion of documents

### Network Security
- Certificate pinning for production
- HTTPS-only connections
- Secure token handling

### Logging
- No sensitive data in logs
- Conditional logging (debug builds only)
- SDK logging disabled in production

## Performance Optimization

### Compose Performance
- Use `remember` and `derivedStateOf` for computed values
- Minimize recomposition scope
- Use `key` parameter in `LazyColumn`
- Avoid creating new lambdas in composables

### Memory Management
- ViewModel scoping prevents leaks
- Proper lifecycle management
- Cancel coroutines on ViewModel clear

### SDK Performance
- Initialize SDK once at app startup
- Reuse SDK instance throughout app
- Cache SDK responses when appropriate

## Dependencies

### Core Dependencies
```kotlin
dependencies {
    // Scytales MID SDK
    implementation("com.scytales.mid:sdk:2.0.0-SNAPSHOT")
    
    // AndroidX Core
    implementation("androidx.core:core-ktx")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    implementation("androidx.activity:activity-compose")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3.adaptive:adaptive-navigation-suite")
    
    // Material Components (required by SDK signup module)
    implementation("com.google.android.material:material")
    
    // CameraX (for QR scanning)
    implementation("androidx.camera:camera-camera2")
    implementation("androidx.camera:camera-lifecycle")
    implementation("androidx.camera:camera-view")
    
    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning")
    
    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions")
}
```

## Build Configuration

### Gradle Setup
```kotlin
android {
    namespace = "com.scytales.mid.sdk.example.app"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.scytales.mid.sdk.example.app"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        
        // Required by SDK for OpenID Connect signup redirect
        manifestPlaceholders["appAuthRedirectScheme"] = "scytales-sdk-example"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    buildFeatures {
        compose = true
        viewBinding = true  // Required by SDK
    }
}
```

## Resources

### Documentation
- [Installation Guide](getting-started/installation.md)
- [Configuration Guide](getting-started/configuration.md)
- [Initialization Guide](getting-started/initialization.md)
- [Document Issuance Guide](features/document-issuance.md)
- [Document Presentation Guide](features/document-presentation.md)
- [Document Management Guide](features/document-management.md)
- [SDK API Reference](api/index.md)

### External References
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Material Design 3](https://m3.material.io/)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)

---

**Last Updated**: 2025-01-21  
**Version**: 2.0.0-SNAPSHOT

