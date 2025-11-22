//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk.manager.signup](index.md)/[signupConfig](signup-config.md)

# signupConfig

[Scytales MID SDK]\
fun [signupConfig](signup-config.md)(block: [SignupConfigBuilder](-signup-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SignupConfig](-signup-config/index.md)

Creates a [SignupConfig](-signup-config/index.md) using a builder DSL.

This is the recommended way to create signup configuration. It provides a clean, Kotlin-idiomatic DSL for configuring FaceTec biometric verification and OpenID Connect authentication settings.

Example usage:

```kotlin
val config = signupConfig {
    facetec {
        deviceKeyIdentifier("my-key-id")
    }
    openIdConnect {
        redirectUri = Uri.parse("myapp://callback")
    }
}
```

#### Return

A fully configured [SignupConfig](-signup-config/index.md) instance.

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | A lambda with [SignupConfigBuilder](-signup-config-builder/index.md) as the receiver that configures     the signup settings. |

#### See also

| |
|---|
| [SignupConfig](-signup-config/index.md) |
| [SignupConfigBuilder](-signup-config-builder/index.md) |

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if required configurations (like OpenID Connect) are not set. |
