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
scytales-mid-sdk = "2.1.0"

[libraries]
scytales-mid-sdk = { module = "com.scytales:scy-mid-sdk", version.ref = "scytales-mid-sdk" }
```

### Step 4: Add SDK Dependency

Add the SDK to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.scytales.mid.sdk)
    
    // Required by mid sdk signup module's XML layouts
    implementation(libs.material)
}
```

#### Optional: FaceTec biometric verification

FaceTec is **not** a transitive dependency of the SDK. Add it only if your issuer's
signup flow includes a face-scan or ID-scan step:

```toml
# gradle/libs.versions.toml
[versions]
facetec-sdk = "9.7.136"

[libraries]
facetec-sdk = { module = "com.facetec:facetec-sdk", version.ref = "facetec-sdk" }
```

```kotlin
// app/build.gradle.kts
implementation(libs.facetec.sdk) { artifact { type = "aar" } }
```

The `artifact { type = "aar" }` clause is required — the artifact is published as an AAR.
See [FaceTec Biometric Verification](../features/facetec.md) for keys and setup.

### Step 5: Configure Build Features

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
- [**Downloading SDK Dependencies**](../features/dependencies.md) - Credentials, CI setup and resolution troubleshooting

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

