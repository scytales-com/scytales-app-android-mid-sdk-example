# Downloading SDK Dependencies

The Scytales MID SDK and the optional FaceTec SDK are not on Maven Central. Both are
served from the Scytales private Cloudsmith feed and require credentials before Gradle
can resolve them.

## What you need

| Item | Where it comes from |
|------|---------------------|
| Cloudsmith username | Scytales, with your license |
| Cloudsmith API key | Scytales, with your license |
| SDK license key | Scytales — see [Configuration](../getting-started/configuration.md) |
| FaceTec keys | Scytales — only if your signup flow has a biometric step |

## Step 1: Declare the repository

The feed is declared once, in `settings.gradle.kts`:

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

`.orNull` means a missing credential resolves to `null` rather than failing
configuration. The build then fails later, during resolution, with
`Username must not be null!` — see [Troubleshooting](#troubleshooting).

## Step 2: Supply credentials

Credentials belong in `~/.gradle/gradle.properties`, **not** in the project — that file
sits outside the repository and is never committed:

```properties
repositoryUser=YOUR-USER-NAME
repositoryPassword=YOUR-API-KEY
```

On CI, pass them as environment variables instead of writing a file. Gradle maps
`ORG_GRADLE_PROJECT_<name>` onto the project property of the same name:

```bash
export ORG_GRADLE_PROJECT_repositoryUser="$CLOUDSMITH_USER"
export ORG_GRADLE_PROJECT_repositoryPassword="$CLOUDSMITH_API_KEY"
```

## Step 3: Declare the artifacts

This project uses a Gradle version catalog. In `gradle/libs.versions.toml`:

```toml
[versions]
scytales-mid-sdk = "2.1.0"
facetec-sdk = "9.7.136"

[libraries]
scytales-mid-sdk = { module = "com.scytales:scy-mid-sdk", version.ref = "scytales-mid-sdk" }
facetec-sdk = { module = "com.facetec:facetec-sdk", version.ref = "facetec-sdk" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
```

> **The artifact id is `scy-mid-sdk`.** Releases from 2.1.0 onward publish under
> `com.scytales:scy-mid-sdk`. Earlier builds used `com.scytales:mid-sdk`; that
> coordinate is no longer updated, so an upgrade has to change the module id as well as
> the version.

Then in `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.scytales.mid.sdk)

    // Optional — only when the signup flow includes a face or ID scan step.
    implementation(libs.facetec.sdk) { artifact { type = "aar" } }

    // Required by the SDK signup module's XML layouts.
    implementation(libs.material)
}
```

The `artifact { type = "aar" }` clause is required for FaceTec: the artifact is
published as an AAR and Gradle would otherwise look for a JAR.

### What the SDK brings in for you

`scy-mid-sdk` declares its own transitive dependencies — EUDI wallet core, Nimbus JOSE
JWT, CBOR, COSE, jMRTD, Ktor, Lottie, ZXing, CameraX, ML Kit text recognition and
AndroidX biometric among them. You do not declare those yourself.

FaceTec is the exception. It is **not** a transitive dependency of the SDK, so the app
must declare it explicitly whenever it is needed. See
[FaceTec Biometric Verification](facetec.md).

## Step 4: Resolve

```bash
./gradlew build
```

To confirm exactly what resolved:

```bash
./gradlew :app:dependencies --configuration debugCompileClasspath
```

Look for these two lines:

```
+--- com.scytales:scy-mid-sdk:2.1.0
+--- com.facetec:facetec-sdk:9.7.136
```

A coordinate followed by `FAILED` did not resolve — read on.

## Troubleshooting

### `Username must not be null!`

Gradle reached the repository with no credentials. Either the properties are absent from
`~/.gradle/gradle.properties`, they are commented out, or the names in that file do not
match the names in `settings.gradle.kts`. Check all three.

### `Could not find com.facetec:facetec-sdk:<version>` (HTTP 404)

That exact version is not published on the feed. List what is actually available:

```bash
curl -u "USER:API-KEY" \
  https://dl.cloudsmith.io/basic/scytales/scytales/maven/com/facetec/facetec-sdk/maven-metadata.xml
```

The same URL shape works for `com/scytales/scy-mid-sdk/maven-metadata.xml`.

### `Remote host terminated the handshake`

A transient network failure against Cloudsmith, not a credentials problem. It surfaces as
a TLS error mentioning `TLSv1.2, TLSv1.3` and tends to appear under
`--refresh-dependencies`, hitting a different artifact each time. Re-run the build. If it
persists across several attempts, check for a proxy or TLS-inspecting firewall between
the machine and `dl.cloudsmith.io`.

### Stale or partial resolution

Force Gradle to re-fetch metadata rather than trusting its cache:

```bash
./gradlew build --refresh-dependencies
```

## Next Steps

- [**SDK Initialization**](initialization.md) - Bring the SDK up at app start
- [**FaceTec Biometric Verification**](facetec.md) - Add the optional biometric step
- [**Installation**](../getting-started/installation.md) - Full project setup, including manifest and build features
