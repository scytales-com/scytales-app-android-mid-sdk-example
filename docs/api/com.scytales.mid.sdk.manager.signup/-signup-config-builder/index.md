//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.signup](../index.md)/[SignupConfigBuilder](index.md)

# SignupConfigBuilder

class [SignupConfigBuilder](index.md)

Builder class for constructing [SignupConfig](../-signup-config/index.md) instances.

This builder provides a fluent API for configuring the signup flow with FaceTec biometric verification and OpenID Connect authentication settings. It uses a DSL pattern to make configuration intuitive and type-safe.

Example usage:

```kotlin
val config = SignupConfigBuilder().apply {
    facetec {
        deviceKeyIdentifier("my-device-key")
        publicFaceScanEncryptionKey("encryption-key")
        customization {
            // FaceTec UI customization
        }
    }
    openIdConnect {
        redirectUri = Uri.parse("myapp://oauth/callback")
    }
}.build()
```

#### See also

| |
|---|
| [SignupConfig](../-signup-config/index.md) |
| [signupConfig](../signup-config.md) |

## Constructors

| | |
|---|---|
| [SignupConfigBuilder](-signup-config-builder.md) | [Scytales MID SDK]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>fun [build](build.md)(): [SignupConfig](../-signup-config/index.md)<br>Builds the [SignupConfig](../-signup-config/index.md) instance. |
| [facetec](facetec.md) | [Scytales MID SDK]<br>fun [facetec](facetec.md)(block: [FacetecConfig.Builder](../../com.scytales.mid.sdk.signup.config/-facetec-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;<br>Configures FaceTec biometric verification settings. |
| [openIdConnect](open-id-connect.md) | [Scytales MID SDK]<br>fun [openIdConnect](open-id-connect.md)(block: [OpenIdConnectConfigBuilder](../-open-id-connect-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;<br>Configures OpenID Connect authentication settings. |
