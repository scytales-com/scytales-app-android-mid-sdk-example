# FaceTec Biometric Verification

FaceTec provides 3D liveness detection and ID-document face matching during Scytales
Manager signup. It is **opt-in**: the SDK does not bundle it, and an app whose signup
flow has no biometric step should ship without it.

## Two modes

### Mode A — without FaceTec

Declare no FaceTec dependency and omit the `facetec { }` block:

```kotlin
signup {
    openIdConnect { redirectUri = "myapp://callback".toUri() }
}
```

The APK is roughly 30–50 MB smaller. If the backend then dispatches a `face_scan` or
`id_scan` step at runtime, the signup wizard stops on its error screen with code
**W00007** (`ERROR_STEP_NOT_CONFIGURED`) and logs a hint naming what is missing. OIDC,
form and sample-data steps are unaffected.

### Mode B — with FaceTec

Scytales issues two keys alongside your SDK license, bound to your `applicationId`:

- `deviceKeyIdentifier`
- `publicFaceScanEncryptionKey`

**1. Add the dependency.** FaceTec is not transitive — the SDK does not pull it in:

```kotlin
// gradle/libs.versions.toml
facetec-sdk = { module = "com.facetec:facetec-sdk", version.ref = "facetec-sdk" }

// app/build.gradle.kts
implementation(libs.facetec.sdk) { artifact { type = "aar" } }
```

The `artifact { type = "aar" }` clause is required; the artifact is published as an AAR.
It resolves from the same Cloudsmith feed as the SDK — see
[Downloading SDK Dependencies](dependencies.md).

**2. Configure it inside the signup block:**

```kotlin
manager {
    organizations = listOf(
        Organization("My Organization", "https://your-scytales-manager.url")
    )

    signup {
        facetec {
            deviceKeyIdentifier = "YOUR-SCYTALES-ISSUED-DEVICE-KEY"
            publicFaceScanEncryptionKey = "YOUR-SCYTALES-ISSUED-PUBLIC-KEY"
        }
        openIdConnect { redirectUri = "myapp://callback".toUri() }
    }
}
```

Both values are required once the block is present — the SDK ships no default keys and
`build()` throws if either is missing. The builder also accepts a function form, if you
prefer it:

```kotlin
facetec {
    deviceKeyIdentifier("YOUR-SCYTALES-ISSUED-DEVICE-KEY")
    publicFaceScanEncryptionKey("YOUR-SCYTALES-ISSUED-PUBLIC-KEY")
}
```

## Making it optional at runtime

Because the block is all-or-nothing, guard it on whether the keys are actually present.
This example app keeps the keys in `SdkConfig` and exposes a computed flag:

```kotlin
/** True when both FaceTec keys are set, in which case biometric signup steps work. */
val isFaceTecConfigured: Boolean
    get() = faceTecDeviceKey.isNotBlank() && faceTecPublicKey.isNotBlank()
```

which `ScytalesSdkInitializer` then checks before configuring anything:

```kotlin
signup {
    openIdConnect {
        redirectUri = SdkConfig.singupOidcRedirectUri.toUri()
    }
    // Only when keys are configured; face and ID scan steps need it.
    if (SdkConfig.isFaceTecConfigured) {
        facetec {
            deviceKeyIdentifier = SdkConfig.faceTecDeviceKey
            publicFaceScanEncryptionKey = SdkConfig.faceTecPublicKey
        }
    }
}
```

This lets one build run in Mode A or Mode B depending on configuration, without a code
change. Note that the *dependency* is still compiled in either way — dropping it is a
build-file change, not a runtime one.

See [`ScytalesSdkInitializer.kt`](../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/ScytalesSdkInitializer.kt)
and [`SdkConfig.kt`](../../app/src/main/java/com/scytales/mid/sdk/example/app/sdk/SdkConfig.kt)
for the full implementation.

## UI customization

By default the SDK applies a Scytales-branded FaceTec theme. Two overrides are available
inside `facetec { }`. They are mutually exclusive — the last one set wins — and both
require the FaceTec dependency on the compile classpath.

### Default — configure nothing

The Scytales theme is applied automatically. This is the out-of-the-box behavior.

### `customize { }` — own the whole theme

The block receives a fresh, blank `FaceTecCustomization` carrying FaceTec's own
defaults, *not* the Scytales theme. Anything you do not set stays at FaceTec's default:

```kotlin
import android.graphics.Color
import com.facetec.sdk.FaceTecCustomization

signup {
    facetec {
        deviceKeyIdentifier = "YOUR-SCYTALES-ISSUED-DEVICE-KEY"
        publicFaceScanEncryptionKey = "YOUR-SCYTALES-ISSUED-PUBLIC-KEY"
        customize {
            ovalCustomization.strokeColor = Color.parseColor("#0977bd")
            // …any FaceTecCustomization field
        }
    }
}
```

### `useExternalCustomization()` — manage it yourself

The SDK applies no customization at all; you call `FaceTecSDK.setCustomization(...)`
directly. Call it any time before the first scan — the SDK never overwrites it:

```kotlin
signup {
    facetec {
        deviceKeyIdentifier = "YOUR-SCYTALES-ISSUED-DEVICE-KEY"
        publicFaceScanEncryptionKey = "YOUR-SCYTALES-ISSUED-PUBLIC-KEY"
        useExternalCustomization()
    }
}

// elsewhere, before the first biometric scan:
FaceTecSDK.setCustomization(myCustomization)
```

## Troubleshooting

### Signup stops with error W00007

The backend asked for a step the app did not configure. For a face or ID scan step that
means the `facetec { }` block is missing or its keys are blank. For an `oidc` step it
means `openIdConnect { }` is missing.

### `Could not find com.facetec:facetec-sdk:<version>`

That version is not on the Cloudsmith feed. See
[Downloading SDK Dependencies](dependencies.md#troubleshooting) for how to list the
published versions.

### Keys rejected at runtime

FaceTec keys are bound to your `applicationId`. Keys issued for one application id will
not work under another — if you changed the id or copied config between flavors, request
matching keys from Scytales.

### `libPhoenixAndroid.so` cannot be stripped

A build-time warning from the NDK's `llvm-strip` against a FaceTec native library. The
library is packaged unstripped and the build succeeds; it is expected and harmless.

## Next Steps

- [**Document Issuance**](document-issuance.md) - The signup flow FaceTec plugs into
- [**SDK Initialization**](initialization.md) - Where the signup block is configured
- [**Downloading SDK Dependencies**](dependencies.md) - Resolving the FaceTec artifact
