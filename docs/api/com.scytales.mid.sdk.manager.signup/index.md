//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk.manager.signup](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [OpenIdConnectConfig](-open-id-connect-config/index.md) | [Scytales MID SDK]<br>data class [OpenIdConnectConfig](-open-id-connect-config/index.md)(val redirectUri: [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html))<br>Configuration for OpenID Connect authentication. |
| [OpenIdConnectConfigBuilder](-open-id-connect-config-builder/index.md) | [Scytales MID SDK]<br>class [OpenIdConnectConfigBuilder](-open-id-connect-config-builder/index.md)<br>Builder class for constructing [OpenIdConnectConfig](-open-id-connect-config/index.md) instances. |
| [SignupConfig](-signup-config/index.md) | [Scytales MID SDK]<br>data class [SignupConfig](-signup-config/index.md)(val facetec: [FacetecConfig](../com.scytales.mid.sdk.signup.config/-facetec-config/index.md), val openIdConnect: [OpenIdConnectConfig](-open-id-connect-config/index.md))<br>Configuration for the signup flow. |
| [SignupConfigBuilder](-signup-config-builder/index.md) | [Scytales MID SDK]<br>class [SignupConfigBuilder](-signup-config-builder/index.md)<br>Builder class for constructing [SignupConfig](-signup-config/index.md) instances. |
| [SignupManager](-signup-manager/index.md) | [Scytales MID SDK]<br>fun interface [SignupManager](-signup-manager/index.md)<br>Functional interface for managing user signup and document issuance flows. |

## Functions

| Name | Summary |
|---|---|
| [openIdConnectConfig](open-id-connect-config.md) | [Scytales MID SDK]<br>fun [openIdConnectConfig](open-id-connect-config.md)(block: [OpenIdConnectConfigBuilder](-open-id-connect-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [OpenIdConnectConfig](-open-id-connect-config/index.md)<br>Creates an [OpenIdConnectConfig](-open-id-connect-config/index.md) using a builder DSL. |
| [signupConfig](signup-config.md) | [Scytales MID SDK]<br>fun [signupConfig](signup-config.md)(block: [SignupConfigBuilder](-signup-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SignupConfig](-signup-config/index.md)<br>Creates a [SignupConfig](-signup-config/index.md) using a builder DSL. |
