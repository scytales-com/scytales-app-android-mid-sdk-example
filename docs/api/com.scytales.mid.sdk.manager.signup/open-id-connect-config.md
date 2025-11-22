//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk.manager.signup](index.md)/[openIdConnectConfig](open-id-connect-config.md)

# openIdConnectConfig

[Scytales MID SDK]\
fun [openIdConnectConfig](open-id-connect-config.md)(block: [OpenIdConnectConfigBuilder](-open-id-connect-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [OpenIdConnectConfig](-open-id-connect-config/index.md)

Creates an [OpenIdConnectConfig](-open-id-connect-config/index.md) using a builder DSL.

This function provides a convenient way to configure OpenID Connect settings for authentication during the signup flow.

Example usage:

```kotlin
val config = openIdConnectConfig {
    redirectUri = Uri.parse("myapp://oauth/callback")
}
```

#### Return

A fully configured [OpenIdConnectConfig](-open-id-connect-config/index.md) instance.

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | A lambda with [OpenIdConnectConfigBuilder](-open-id-connect-config-builder/index.md) as the receiver that     configures the OpenID Connect settings. |

#### See also

| |
|---|
| [OpenIdConnectConfig](-open-id-connect-config/index.md) |
| [OpenIdConnectConfigBuilder](-open-id-connect-config-builder/index.md) |

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if the redirect URI is not set. |
