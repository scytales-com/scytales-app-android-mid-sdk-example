# Configuration

Configure the Scytales MID SDK by setting up the `SdkConfig` object with your license key and organization URL.

## SdkConfig Object

The SDK uses a centralized configuration object to store credentials and settings. Create this file in your project:

**File**: `app/src/main/java/com/scytales/mid/sdk/example/app/sdk/SdkConfig.kt`

```kotlin
package com.scytales.mid.sdk.example.app.sdk

object SdkConfig {
    /**
     * Scytales license key (MANDATORY)
     * Contact Scytales support to obtain your license key
     */
    const val licenseKey: String = "your-license-key-here"

    /**
     * Organization base URL for Scytales Manager API (MANDATORY)
     * Contact Scytales support to obtain your organization URL
     */
    const val organizationUrl: String = "https://your-scytales-manager.url"

    /**
     * OIDC Redirect URI for signup flow with OpenID Connect (MANDATORY)
     * 
     * This value must match:
     * 1. The manifestPlaceholders in build.gradle.kts
     * 2. The OIDC client configuration in Scytales Manager
     * 
     * Format: {scheme}://{host}/{path}
     * Example: "com.scytales://wallet/oidc"
     */
    const val singupOidcRedirectUri: String = "com.scytales://wallet/oidc"
}
```

[View full implementation](../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/SdkConfig.kt)

## Configuration Properties

### Mandatory Properties

All three properties are required for SDK initialization. The SDK will fail to initialize if any mandatory property is missing or blank.

| Property | Type | Description |
|----------|------|-------------|
| `licenseKey` | `String` | Your Scytales SDK license key. Environment-specific (dev/staging/prod). |
| `organizationUrl` | `String` | Base URL for the Scytales Manager API. Used for all Manager features. |
| `singupOidcRedirectUri` | `String` | Redirect URI for OpenID Connect signup flow. Must match build.gradle.kts manifestPlaceholders and Scytales Manager OIDC client settings. |

**How to Obtain:**
- Contact Scytales support to receive your license key and organization URL
- Keys are tied to specific environments and should not be shared
- The redirect URI must be registered in your Scytales Manager OIDC client configuration

## Build Configuration

### Manifest Placeholders for OIDC Redirect

The OpenID Connect signup flow requires configuring manifest placeholders in your `app/build.gradle.kts`:

**File**: `app/build.gradle.kts`

```kotlin
android {
    defaultConfig {
        // ... other configurations
        
        // Required by Scytales MID SDK for OpenID Connect signup redirect
        manifestPlaceholders["appAuthRedirectScheme"] = "com.scytales"
        manifestPlaceholders["appAuthRedirectHost"] = "wallet"
        manifestPlaceholders["appAuthRedirectPath"] = "/oidc"
    }
}
```

**How it works:**
- These placeholders construct the redirect URI: `com.scytales://wallet/oidc`
- The URI must **exactly match** `SdkConfig.singupOidcRedirectUri`
- Used by `RedirectUriReceiverActivity` to handle OAuth callbacks
- Must be registered in your Scytales Manager OIDC client configuration

**Customization:**
You can customize the redirect URI by changing these values, but ensure:
1. Update `SdkConfig.singupOidcRedirectUri` to match
2. Register the new URI in Scytales Manager
3. Use a unique scheme to avoid conflicts with other apps

**Example with custom scheme:**

```kotlin
// In build.gradle.kts
manifestPlaceholders["appAuthRedirectScheme"] = "com.yourcompany.app"
manifestPlaceholders["appAuthRedirectHost"] = "auth"
manifestPlaceholders["appAuthRedirectPath"] = "/callback"

// In SdkConfig.kt
const val singupOidcRedirectUri: String = "com.yourcompany.app://auth/callback"
```

## Secure Configuration

**⚠️ Important**: Never commit production credentials to version control.

### Recommended Approach: Environment Variables

**1. Create `local.properties`** (add to `.gitignore`):

```properties
# local.properties (DO NOT COMMIT)
scytales.license.key=your-license-key
scytales.organization.url=https://your-scytales-manager.url
scytales.facetec.device.key=your-device-key
scytales.facetec.public.key=your-public-key
```

**2. Load in `build.gradle.kts`**:

```kotlin
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

android {
    defaultConfig {
        buildConfigField("String", "LICENSE_KEY", 
            "\"${localProperties.getProperty("scytales.license.key", "")}\"")
        buildConfigField("String", "ORGANIZATION_URL", 
            "\"${localProperties.getProperty("scytales.organization.url", "")}\"")
    }
}
```

**3. Use in `SdkConfig.kt`**:

```kotlin
object SdkConfig {
    const val licenseKey: String = BuildConfig.LICENSE_KEY
    const val organizationUrl: String = BuildConfig.ORGANIZATION_URL
    // ... other properties
}
```

### Alternative: Build Variants

Configure different credentials per build type:

```kotlin
android {
    buildTypes {
        debug {
            buildConfigField("String", "LICENSE_KEY", "\"dev-license-key\"")
            buildConfigField("String", "ORGANIZATION_URL", "\"https://dev.scytales-manager.url\"")
        }
        release {
            buildConfigField("String", "LICENSE_KEY", "\"prod-license-key\"")
            buildConfigField("String", "ORGANIZATION_URL", "\"https://prod.scytales-manager.url\"")
        }
    }
}
```

## Configuration Validation

The SDK validates all configuration during initialization. Common errors:

```kotlin
// Missing license key
SdkInitializationError.InvalidLicense("License key not configured")

// Missing organization URL
SdkInitializationError.InvalidOrganization("Organization URL not configured")

// Invalid license format
SdkInitializationError.InvalidLicense("License validation failed")
```

## Additional Configuration

**OpenID4VCI Issuers**: Configured during SDK initialization in the `wallet` block. See [Initialization Guide](initialization.md#openid4vci-configuration).

**Presentation Protocols**: Configured during SDK initialization (Proximity, OpenID4VP, DCAPI). See [Initialization Guide](initialization.md).

## Next Steps

- [**Initialization**](initialization.md) - Initialize the SDK with your configuration
- [**Example Implementation**](../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/SdkConfig.kt) - View complete code

