//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.signup](../index.md)/[OpenIdConnectConfig](index.md)

# OpenIdConnectConfig

data class [OpenIdConnectConfig](index.md)(val redirectUri: [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html))

Configuration for OpenID Connect authentication.

This data class contains the OAuth/OIDC settings required for authenticating users during the signup flow. The redirect URI is used by the authentication provider to redirect back to the app after successful authentication.

#### See also

| |
|---|
| [OpenIdConnectConfigBuilder](../-open-id-connect-config-builder/index.md) |
| [openIdConnectConfig](../open-id-connect-config.md) |

## Constructors

| | |
|---|---|
| [OpenIdConnectConfig](-open-id-connect-config.md) | [Scytales MID SDK]<br>constructor(redirectUri: [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html)) |

## Properties

| Name | Summary |
|---|---|
| [redirectUri](redirect-uri.md) | [Scytales MID SDK]<br>val [redirectUri](redirect-uri.md): [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html)<br>The URI to which the authentication provider will redirect     after successful authentication. This must be a deep link     registered with your app (e.g., &quot;myapp://oauth/callback&quot;). |
