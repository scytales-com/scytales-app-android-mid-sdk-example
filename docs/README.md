# Scytales MID SDK Documentation

Documentation for integrating the Scytales Mobile Identity (MID) SDK into Android
applications.

- **SDK Version**: 2.1.0 (`com.scytales:scy-mid-sdk`)
- **Source Code**: Browse [`app/src/main/java/`](../app/src/main/java/com/scytales/mid/sdk/example/app/)
- **Example Application**: See the root [`README.md`](../README.md)

Every guide here is backed by the [Scytales MID SDK Android Example](../README.md)
application, so each snippet comes from code that compiles against the SDK version above.

## Getting Started

Setting up and initializing the SDK:

- [**Installation**](getting-started/installation.md) - Add the SDK to your Android project
- [**Configuration**](getting-started/configuration.md) - Configure license and SDK options
- [**Initialization**](getting-started/initialization.md) - Initialize the SDK in your application

## Feature Guides

Implementing specific SDK features:

- [**Downloading SDK Dependencies**](features/dependencies.md) - Cloudsmith credentials, artifact coordinates, resolution troubleshooting
- [**SDK Initialization**](features/initialization.md) - License, wallet, manager and protocol configuration
- [**FaceTec Biometric Verification**](features/facetec.md) - Optional biometric signup, keys and UI customization
- [**Document Issuance**](features/document-issuance.md) - Issue credentials via Scytales Manager and OpenID4VCI
- [**Document Presentation**](features/document-presentation.md) - Present credentials via Proximity (BLE), Remote (OpenID4VP) and DCAPI
- [**Document Management**](features/document-management.md) - List, view and delete documents

## Reference

- [**Architecture Guide**](architecture.md) - Application architecture and design patterns
