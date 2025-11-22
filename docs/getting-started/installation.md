# Installation

Add the Scytales MID SDK to your Android project.

## Prerequisites

- **Android Studio**: Ladybug or later
- **JDK**: 11 or higher
- **Min SDK**: API 28 (Android 9.0)
- **Target SDK**: API 36
- **Kotlin**: 2.2.21

## Add SDK Dependency

### Step 1: Configure Maven Repository

The SDK is distributed via a private Maven repository. Add the repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://dl.cloudsmith.io/basic/scytales/scytales/maven/")
            credentials {
                username = providers.gradleProperty("repositoryUser").orNull
                password = providers.gradleProperty("repositoryPassword").orNull
            }
        }
    }
}
```

### Step 2: Configure Repository Credentials

The repository requires authentication. Create or add to `~/.gradle/gradle.properties`:

```properties
repositoryUser=YOUR-USER-NAME
repositoryPassword=YOUR-API-KEY
```

**Note**: Replace `YOUR-USER-NAME` and `YOUR-API-KEY` with your actual repository credentials.

### Step 3: Add SDK to Version Catalog

Add the SDK version to `gradle/libs.versions.toml`:

```toml
[versions]
scytales-mid-sdk = "2.0.0-SNAPSHOT"

[libraries]
scytales-mid-sdk = { module = "com.scytales:mid-sdk", version.ref = "scytales-mid-sdk" }
```

### Step 3: Add SDK Dependency

Add the SDK to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.scytales.mid.sdk)
    
    // Required by mid sdk signup module's XML layouts
    implementation(libs.material)
}
```

### Step 4: Configure Build Features

Enable required build features in `build.gradle.kts`:

```kotlin
android {
    buildFeatures {
        compose = true
        viewBinding = true // Required by SDK
    }
}
```

### Step 6: Configure AndroidManifest

Add `tools:replace` attribute to handle theme override:

```xml
<application
    android:name=".YourApplication"
    android:theme="@style/YourTheme"
    tools:replace="android:theme">
    <!-- Required by SDK -->
</application>
```

Add the `tools` namespace at the top of `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
```

### Step 7: Configure Redirect Scheme

Add the app authentication redirect scheme in `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        // Required by SDK for OpenID Connect signup redirect
        manifestPlaceholders["appAuthRedirectScheme"] = "your-app-scheme"
        manifestPlaceholders["appAuthRedirectHost"] = "your-app-host"
        manifestPlaceholders["appAuthRedirectPath"] = "/auth/callback"
    }
}
```

**Example**: `"scytales-sdk-example"`

## Sync Project

Sync your Gradle files to download the SDK:

```bash
./gradlew build
```

## Verify Installation

Verify the SDK is available by checking imports:

```kotlin
import com.scytales.mid.sdk.Sdk
import com.scytales.mid.sdk.license.LicenseConfig
```

If imports resolve successfully, the SDK is installed correctly.

## Next Steps

- [**Configuration**](configuration.md) - Configure the SDK license and options
- [**Initialization**](initialization.md) - Initialize the SDK in your application

## Troubleshooting

### SDK Not Found

If the SDK is not found:
- Verify the private Maven repository is correctly configured in `settings.gradle.kts`
- Check your credentials in `~/.gradle/gradle.properties` are correct
- Check the SDK version in version catalog matches the available version
- Run `./gradlew build --refresh-dependencies`

### Build Feature Conflicts

If you encounter build feature conflicts:
- Ensure `viewBinding = true` is set
- Verify `tools:replace="android:theme"` is in `AndroidManifest.xml`

### Manifest Merge Issues

If manifest merge fails:
- Add `xmlns:tools` namespace to manifest
- Check for conflicting themes or application attributes

