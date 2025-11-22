//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkConfigBuilder](index.md)/[manager](manager.md)

# manager

[Scytales MID SDK]\
open override fun [manager](manager.md)(block: [ScytalesManagerConfigBuilder](../../com.scytales.mid.sdk.manager/-scytales-manager-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;

Configure Scytales manager settings using a DSL block.

This method provides access to the [ScytalesManagerConfigBuilder](../../com.scytales.mid.sdk.manager/-scytales-manager-config-builder/index.md) for configuring proprietary Scytales features. The configuration is required for using any Scytales-specific functionality.

Configuration options include:

- 
   Organization settings (defaults to ScytalesOrganization)
- 
   Signup configuration with FaceTec biometric enrollment
- 
   OpenID Connect settings for document issuance workflows
- 
   Custom biometric enrollment workflows

The manager configuration is mandatory and must be provided before calling [build](build.md).

#### Return

This builder instance for method chaining

Example:

```kotlin
manager {
    organization = Organization.id("your-organization-id")
    signup {
        facetec {
            deviceKeyIdentifier("your-device-key")
            publicFaceScanEncryptionKey("your-encryption-key")
        }
        openIdConnect {
            redirectUri = Uri.parse("your-app://callback")
        }
    }
}
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | Configuration lambda with [ScytalesManagerConfigBuilder](../../com.scytales.mid.sdk.manager/-scytales-manager-config-builder/index.md) as receiver |

[Scytales MID SDK]\
open override fun [manager](manager.md)(config: [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)): &lt;Error class: unknown class&gt;

Set Scytales manager configuration directly from a pre-configured instance.

Use this method when you have constructed a [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md) instance separately and want to use it as-is rather than building it through the DSL. This is useful for sharing configurations across multiple SDK instances or when configuration comes from external sources.

#### Return

This builder instance for method chaining

Example:

```kotlin
val sharedManagerConfig = scytalesManagerConfig {
    organization = Organization.id("your-organization-id")
    signup {
        facetec { /* ... */}
        openIdConnect { /* ... */}
    }
}

manager(sharedManagerConfig)
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| config | Pre-configured [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md) instance |
