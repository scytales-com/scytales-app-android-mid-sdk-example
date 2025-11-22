# Scytales MID SDK - Android Example

A reference Android application demonstrating integration of the Scytales Mobile Identity (MID) SDK for digital identity wallet functionality. Built with Jetpack Compose and Kotlin, following modern Android development practices.

## Overview

This example application showcases how to integrate the Scytales MID SDK to build a digital identity wallet that supports:

- Issuing and storing digital credentials (mDL, SD-JWT-VC)
- Presenting credentials in person (proximity) and online (remote)
- Managing multiple document types and credentials
- Biometric enrollment and authentication

**SDK Version:** 2.0.0-SNAPSHOT

## Features

### Supported Document Formats

| Format | Description | Status |
|--------|-------------|--------|
| **ISO mDL (mso_mdoc)** | ISO/IEC 18013-5 mobile driving license | ✅ Full support |
| **SD-JWT-VC** | Selective Disclosure JWT Verifiable Credentials | ✅ Full support |

### Document Issuance Methods

| Method | Description | Features | Implementation |
|--------|-------------|----------|----------------|
| **Scytales Manager** | Direct issuance via organization backend with biometric enrollment | Organization-based issuance, FaceTec biometric verification | ✅ [`DocumentTypesScreen.kt`](app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/manager/DocumentTypesScreen.kt) |
| **OpenID4VCI v1.0** | Standard credential offer protocol with QR code scanning | Authorization Code Flow, Pre-authorization Flow, DPoP JWT, Batch issuance, Deferred issuance | ✅ [`OfferReviewScreen.kt`](app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/openid4vci/OfferReviewScreen.kt) |

### Document Presentation Methods

| Method | Protocol | Features | Implementation |
|--------|----------|----------|----------------|
| **Proximity** | ISO 18013-5 over BLE | Device engagement via QR/NFC, BLE peripheral/central mode, Selective disclosure | ✅ [`ProximityPresentationScreen.kt`](app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/proximity/ProximityPresentationScreen.kt) |
| **Remote** | OpenID4VP 1.0 over HTTPS | Client ID schemes (preregistered, x509_san_dns, x509_hash, redirect_uri), DCQL support | ✅ [`RemotePresentationScreen.kt`](app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/remote/RemotePresentationScreen.kt) |
| **DCAPI** | ISO/IEC TS 18013-7:2025 (org-iso-mdoc) | Browser-initiated requests, System credential picker integration | ✅ [`DCAPIPresentationScreen.kt`](app/src/main/java/com/scytales/mid/sdk/example/app/ui/screens/dcapi/DCAPIPresentationScreen.kt) |

### Document Management

| Feature | Description | Details |
|---------|-------------|---------|
| **List Documents** | Display all issued credentials | Filter by type, format, or custom predicate |
| **View Details** | Show document claims and metadata | Access namespaces, claims, format info, security properties |
| **Delete Documents** | Remove credentials from wallet | Secure deletion with Keystore key removal |
| **Batch Credentials** | Multiple credentials per document | Support for credential rotation and one-time use policies |
| **Secure Storage** | Android Keystore-backed encryption | StrongBox support, user authentication options |

## Technology Stack

- **SDK**: Scytales MID SDK 2.0.0-SNAPSHOT
- **Language**: Kotlin 2.2.21
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Clean Architecture principles
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36

## Quick Start

### Prerequisites

- Android Studio Ladybug or later
- JDK 11+
- Android device or emulator (API 28+)
- Scytales SDK license key

### Setup

1. **Clone and open the project**
   ```bash
   git clone <repository-url>
   cd scytales-app-android-mid-sdk-example
   ```

2. **Configure SDK** (Mandatory)
   
   Edit [`SdkConfig.kt`](app/src/main/java/com/scytales/mid/sdk/example/app/sdk/SdkConfig.kt):
   ```kotlin
   object SdkConfig {
       val licenseKey: String = "your-license-key-here" // Mandatory
       val organizationUrl: String = "https://your-scytales-manager.url" // Mandatory
   }
   ```

3. **Build and run**
   ```bash
   ./gradlew installDebug
   ```

### First Steps

After installation, the app will:
1. Initialize the SDK with your license
2. Display the home screen (empty initially)
3. Allow you to add documents via the + button

See the [Getting Started Guide](docs/getting-started/initialization.md) for detailed configuration options.

## Project Structure

```
app/src/main/java/com/scytales/mid/sdk/example/app/
├── MainActivity.kt              # Navigation coordinator
├── ScytalesApp.kt              # Application class with SDK init
├── sdk/                        # SDK initialization
│   ├── ScytalesSdkInitializer.kt
│   ├── SdkConfig.kt
│   └── SdkState.kt
├── ui/screens/                 # Feature screens
│   ├── home/                   # Document list
│   ├── manager/                # Scytales Manager issuance
│   ├── openid4vci/             # OpenID4VCI issuance
│   ├── proximity/              # BLE presentation
│   ├── remote/                 # OpenID4VP presentation
│   ├── dcapi/                  # Browser presentation
│   └── documentdetails/        # Document viewer
└── openid4vci/                 # OpenID4VCI coordinator
```

## Documentation

### Integration Guides

- [**Installation**](docs/getting-started/installation.md) - Add SDK to your project
- [**Configuration**](docs/getting-started/configuration.md) - Configure license and options
- [**Initialization**](docs/getting-started/initialization.md) - Initialize the SDK

### Feature Guides

- [**Document Issuance**](docs/features/document-issuance.md) - Issue credentials via Scytales Manager and OpenID4VCI
- [**Document Presentation**](docs/features/document-presentation.md) - Present credentials via Proximity, Remote, and DCAPI
- [**Document Management**](docs/features/document-management.md) - List, view, and delete documents

### Reference

- [**Architecture Overview**](docs/architecture.md) - Application architecture and patterns
- [**SDK API Reference**](docs/api/index.md) - Complete SDK documentation

## Requirements

- **Android Studio**: Ladybug or later
- **JDK**: 11 or higher
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36
- **Kotlin**: 2.2.21

## License

This example application is provided for demonstration purposes. See license file for details.

## Support

- **Documentation**: See the [`docs/`](docs/) folder
- **SDK Reference**: Browse the [SDK API documentation](docs/api/index.md)
- **Scytales Support**: Contact Scytales for SDK-specific questions

---

**Built with** [Jetpack Compose](https://developer.android.com/jetpack/compose) • **Powered by** [Scytales MID SDK](https://scytales.com)

