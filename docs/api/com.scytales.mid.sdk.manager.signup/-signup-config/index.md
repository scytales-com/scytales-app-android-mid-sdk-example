//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.signup](../index.md)/[SignupConfig](index.md)

# SignupConfig

data class [SignupConfig](index.md)(val facetec: [FacetecConfig](../../com.scytales.mid.sdk.signup.config/-facetec-config/index.md), val openIdConnect: [OpenIdConnectConfig](../-open-id-connect-config/index.md))

Configuration for the signup flow.

This data class encapsulates all necessary configuration settings for user enrollment and document issuance, including biometric verification (FaceTec) and authentication (OpenID Connect) settings.

Use the [signupConfig](../signup-config.md) DSL function to create instances:

```kotlin
val config = signupConfig {
    facetec {
        // Configure FaceTec settings
    }
    openIdConnect {
        redirectUri = Uri.parse("myapp://callback")
    }
}
```

#### See also

| |
|---|
| [SignupConfigBuilder](../-signup-config-builder/index.md) |
| [signupConfig](../signup-config.md) |
| [FacetecConfig](../../com.scytales.mid.sdk.signup.config/-facetec-config/index.md) |
| [OpenIdConnectConfig](../-open-id-connect-config/index.md) |

## Constructors

| | |
|---|---|
| [SignupConfig](-signup-config.md) | [Scytales MID SDK]<br>constructor(facetec: [FacetecConfig](../../com.scytales.mid.sdk.signup.config/-facetec-config/index.md), openIdConnect: [OpenIdConnectConfig](../-open-id-connect-config/index.md)) |

## Properties

| Name | Summary |
|---|---|
| [facetec](facetec.md) | [Scytales MID SDK]<br>val [facetec](facetec.md): [FacetecConfig](../../com.scytales.mid.sdk.signup.config/-facetec-config/index.md)<br>The FaceTec biometric verification configuration used for liveness     detection and identity verification during signup. |
| [openIdConnect](open-id-connect.md) | [Scytales MID SDK]<br>val [openIdConnect](open-id-connect.md): [OpenIdConnectConfig](../-open-id-connect-config/index.md)<br>The OpenID Connect authentication configuration used for     user authentication with the issuer. |
