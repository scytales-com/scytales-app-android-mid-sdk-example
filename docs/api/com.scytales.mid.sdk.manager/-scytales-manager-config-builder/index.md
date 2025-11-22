//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager](../index.md)/[ScytalesManagerConfigBuilder](index.md)

# ScytalesManagerConfigBuilder

class [ScytalesManagerConfigBuilder](index.md)

Builder class for constructing [ScytalesManagerConfig](../-scytales-manager-config/index.md) instances.

This builder provides a fluent API for configuring the Scytales Manager with organization and signup settings. It uses the builder pattern to ensure all required configurations are properly set before creating the final configuration object.

Example usage:

```kotlin
val config = scytalesManagerConfig {
    organization = Organization.url("https://your-scytales-manager.url")
    signup {
        // signup configuration
    }
}
```

#### See also

| |
|---|
| [ScytalesManagerConfig](../-scytales-manager-config/index.md) |
| [scytalesManagerConfig](../scytales-manager-config.md) |

## Constructors

| | |
|---|---|
| [ScytalesManagerConfigBuilder](-scytales-manager-config-builder.md) | [Scytales MID SDK]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [organization](organization.md) | [Scytales MID SDK]<br>var [organization](organization.md): [Organization](../-organization/index.md)?<br>The organization configuration. Must be set before building. |
| [signupConfig](signup-config.md) | [Scytales MID SDK]<br>var [signupConfig](signup-config.md): [SignupConfig](../../com.scytales.mid.sdk.manager.signup/-signup-config/index.md)?<br>The signup configuration. Must be set before building. |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>fun [build](build.md)(): [ScytalesManagerConfig](../-scytales-manager-config/index.md)<br>Builds the [ScytalesManagerConfig](../-scytales-manager-config/index.md) instance. |
| [signup](signup.md) | [Scytales MID SDK]<br>fun [signup](signup.md)(block: [SignupConfigBuilder](../../com.scytales.mid.sdk.manager.signup/-signup-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;<br>Configures the signup settings using a builder DSL. |
