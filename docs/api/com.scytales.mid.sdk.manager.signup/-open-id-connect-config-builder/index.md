//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.signup](../index.md)/[OpenIdConnectConfigBuilder](index.md)

# OpenIdConnectConfigBuilder

class [OpenIdConnectConfigBuilder](index.md)

Builder class for constructing [OpenIdConnectConfig](../-open-id-connect-config/index.md) instances.

This builder provides a simple way to configure OpenID Connect authentication settings for the signup flow.

Example usage:

```kotlin
val config = OpenIdConnectConfigBuilder().apply {
    redirectUri = Uri.parse("myapp://oauth/callback")
}.build()
```

#### See also

| |
|---|
| [OpenIdConnectConfig](../-open-id-connect-config/index.md) |
| [openIdConnectConfig](../open-id-connect-config.md) |

## Constructors

| | |
|---|---|
| [OpenIdConnectConfigBuilder](-open-id-connect-config-builder.md) | [Scytales MID SDK]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [redirectUri](redirect-uri.md) | [Scytales MID SDK]<br>var [redirectUri](redirect-uri.md): [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html)?<br>The OAuth redirect URI. Must be set before building. This should be a deep link registered with your app. |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>fun [build](build.md)(): [OpenIdConnectConfig](../-open-id-connect-config/index.md)<br>Builds the [OpenIdConnectConfig](../-open-id-connect-config/index.md) instance. |
