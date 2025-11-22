//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk.manager](index.md)/[scytalesManagerConfig](scytales-manager-config.md)

# scytalesManagerConfig

[Scytales MID SDK]\
fun [scytalesManagerConfig](scytales-manager-config.md)(block: [ScytalesManagerConfigBuilder](-scytales-manager-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [ScytalesManagerConfig](-scytales-manager-config/index.md)

Creates a [ScytalesManagerConfig](-scytales-manager-config/index.md) using a builder DSL.

This is the recommended way to create a Scytales Manager configuration. It provides a clean, Kotlin-idiomatic DSL for configuring all manager settings.

Example usage:

```kotlin
val config = scytalesManagerConfig {
    organization = Organization.url("https://your-scytales-manager.url")
    signup {
        facetec {
            // configure FaceTec settings
        }
        openIdConnect {
            redirectUri = Uri.parse("myapp://callback")
        }
    }
}
```

#### Return

A fully configured [ScytalesManagerConfig](-scytales-manager-config/index.md) instance.

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | A lambda with [ScytalesManagerConfigBuilder](-scytales-manager-config-builder/index.md) as the receiver that configures     the manager settings. |

#### See also

| |
|---|
| [ScytalesManagerConfig](-scytales-manager-config/index.md) |
| [ScytalesManagerConfigBuilder](-scytales-manager-config-builder/index.md) |

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if required configurations (organization or signup) are not set. |
