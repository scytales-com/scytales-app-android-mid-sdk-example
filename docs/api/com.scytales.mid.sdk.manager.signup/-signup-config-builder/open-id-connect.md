//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.signup](../index.md)/[SignupConfigBuilder](index.md)/[openIdConnect](open-id-connect.md)

# openIdConnect

[Scytales MID SDK]\
fun [openIdConnect](open-id-connect.md)(block: [OpenIdConnectConfigBuilder](../-open-id-connect-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;

Configures OpenID Connect authentication settings.

This method is required and must be called to set the authentication configuration, including the OAuth redirect URI used during the authentication flow.

#### Return

This builder instance for method chaining.

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | A lambda with [OpenIdConnectConfigBuilder](../-open-id-connect-config-builder/index.md) as the receiver     that configures the OpenID Connect settings. |

#### See also

| |
|---|
| [OpenIdConnectConfig](../-open-id-connect-config/index.md) |
| [OpenIdConnectConfigBuilder](../-open-id-connect-config-builder/index.md) |
