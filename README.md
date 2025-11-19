# Scytales MID SDK - Android Example Application

A modern Android example application demonstrating the integration and usage of the Scytales MID SDK using Jetpack Compose and Kotlin.

## 📱 Project Overview

This project showcases best practices for Android development with the Scytales MID SDK, including:
- Modern UI with Jetpack Compose and Material Design 3
- Clean Architecture with MVVM pattern
- Adaptive navigation for different screen sizes
- Type-safe development with Kotlin

## 🛠️ Technology Stack

- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Build System**: Gradle with Kotlin DSL
- **Min SDK**: 28 (Android 9.0 Pie)
- **Target SDK**: 36
- **JVM Target**: 11

### Key Dependencies

- Scytales MID SDK 2.0.0-SNAPSHOT
- AndroidX Core KTX 1.17.0
- Jetpack Compose BOM 2024.09.00
- Material3 Adaptive Navigation Suite
- Lifecycle Runtime KTX 2.9.4

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or later
- JDK 11 or later
- Android SDK with API level 28+
- Gradle 8.13.1+

### Setup

1. **Clone the repository**
   ```powershell
   git clone <repository-url>
   cd scytales-app-android-mid-sdk-example
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio
   - Wait for Gradle sync to complete

3. **Configure Scytales SDK** (if needed)
   - Add SDK credentials/configuration as per SDK documentation
   - Update `local.properties` with any required keys

4. **Build and Run**
   ```powershell
   # Build the project
   ./gradlew build
   
   # Install on connected device/emulator
   ./gradlew installDebug
   ```

## 📂 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/scytales/mid/sdk/example/app/
│   │   │   ├── MainActivity.kt              # Main entry point
│   │   │   ├── ui/
│   │   │   │   ├── theme/                   # App theming
│   │   │   │   ├── screens/                 # Screen composables
│   │   │   │   └── components/              # Reusable components
│   │   │   ├── data/                        # Data layer
│   │   │   ├── domain/                      # Business logic
│   │   │   └── util/                        # Utilities
│   │   ├── res/                             # Resources
│   │   └── AndroidManifest.xml
│   ├── test/                                # Unit tests
│   └── androidTest/                         # Instrumented tests
└── build.gradle.kts                         # App-level build config
```

## 🤖 GitHub Copilot Agent Configuration

This project includes a specialized **Android Developer Agent** configuration for GitHub Copilot to assist with development tasks.

### Agent Files

The `.github/` directory contains comprehensive agent configuration:

- **`copilot-instructions.md`**: Detailed development guidelines and best practices
- **`copilot-agent-config.json`**: Structured agent capabilities and workflows
- **`ARCHITECTURE.md`**: Architecture patterns and design decisions
- **`QUICKREF.md`**: Quick reference for common tasks
- **`README.md`**: Agent usage guide

### Agent Capabilities

The Android Developer Agent can help you:

- ✅ Create and modify Jetpack Compose UI components
- ✅ Implement navigation and state management
- ✅ Integrate Scytales MID SDK features
- ✅ Write unit and UI tests
- ✅ Debug Android-specific issues
- ✅ Manage Gradle dependencies
- ✅ Implement Material3 design patterns
- ✅ Optimize performance and memory usage
- ✅ Follow Android security best practices
- ✅ Create responsive layouts for all screen sizes

### Using the Agent

Simply ask GitHub Copilot for help with Android development tasks:

```
"Create a login screen with Material3 components"
"Add a ViewModel for managing user state"
"Integrate the Scytales SDK authentication feature"
"Write UI tests for the home screen"
```

See [`.github/README.md`](.github/README.md) for detailed usage instructions.

## 🏗️ Architecture

This project follows **Clean Architecture** principles with **MVVM** pattern:

- **Presentation Layer**: Composables and ViewModels for UI
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories and data sources

See [`.github/ARCHITECTURE.md`](.github/ARCHITECTURE.md) for detailed architecture documentation.

## 🎨 Features

### Current Implementation

- ✅ Material Design 3 theming
- ✅ Adaptive navigation (bottom bar, rail, drawer)
- ✅ Edge-to-edge display
- ✅ Multiple navigation destinations
- ✅ Responsive UI for different screen sizes
- ✅ Preview support for all composables

### Planned Features

- ⏳ Scytales SDK authentication
- ⏳ User profile management
- ⏳ Secure data storage
- ⏳ Offline support
- ⏳ Push notifications
- ⏳ Biometric authentication

## 🧪 Testing

### Running Tests

```powershell
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew jacocoTestReport
```

### Test Structure

- **Unit Tests**: Business logic, ViewModels, Use Cases
- **Integration Tests**: Repository and data layer
- **UI Tests**: Composable rendering and interactions

## 📋 Development Workflow

### Adding a New Feature

1. **Create the UI** - Design composable components
2. **Implement ViewModel** - Handle state and events
3. **Add Use Cases** - Implement business logic
4. **Create Repository** - Manage data access
5. **Write Tests** - Unit and UI tests
6. **Update Navigation** - Add to navigation structure

See [`.github/QUICKREF.md`](.github/QUICKREF.md) for quick reference.

## 🔧 Common Tasks

### Adding a Gradle Dependency

1. Add version to `gradle/libs.versions.toml`
2. Define library in `[libraries]` section
3. Add to `app/build.gradle.kts`
4. Sync Gradle

### Creating a New Screen

1. Create composable in `ui/screens/`
2. Add to `AppDestinations` enum
3. Update navigation logic
4. Add ViewModel if needed
5. Write tests

### Debugging

- Use Logcat: `adb logcat -s YourTag`
- Use Android Studio Debugger
- Check Layout Inspector for UI issues
- Profile with Android Profiler

## 📚 Documentation

- [Architecture Guide](.github/ARCHITECTURE.md) - Detailed architecture documentation
- [Quick Reference](.github/QUICKREF.md) - Common tasks and patterns
- [Agent Guide](.github/README.md) - GitHub Copilot agent usage
- [Coding Instructions](.github/copilot-instructions.md) - Development guidelines

## 🤝 Contributing

### Code Style

- Follow Kotlin coding conventions
- Use 4-space indentation
- Keep lines under 120 characters
- Use meaningful names
- Write self-documenting code

### Commit Messages

Use conventional commit format:
- `feat:` New features
- `fix:` Bug fixes
- `refactor:` Code refactoring
- `test:` Test additions/modifications
- `docs:` Documentation changes
- `chore:` Build/tooling changes

## 🔒 Security

- Never commit sensitive data (API keys, secrets)
- Use Android Keystore for secure storage
- Follow OWASP Mobile Security guidelines
- Keep dependencies updated
- Implement certificate pinning for production

## 📄 License

[Add your license information here]

## 📞 Support

For issues with:
- **The app**: Create an issue in this repository
- **Scytales SDK**: Refer to SDK documentation or contact Scytales support
- **Android development**: Check [Android Developer Documentation](https://developer.android.com)

## 🙏 Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Powered by [Scytales MID SDK](https://scytales.com)
- Follows [Material Design 3](https://m3.material.io/) guidelines

---

**Note**: This is an example application for demonstration purposes. Adapt it to your specific needs and requirements.

