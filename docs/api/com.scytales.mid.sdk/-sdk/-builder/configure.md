//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[Sdk](../index.md)/[Builder](index.md)/[configure](configure.md)

# configure

[Scytales MID SDK]\
abstract fun [configure](configure.md)(configure: [SdkConfig.Builder](../../-sdk-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](index.md)

Configure the SDK using a unified configuration DSL.

This is the primary configuration method that allows you to set up both the Wallet and Scytales manager in a single, cohesive configuration block with type-safe and validated settings.

This method must be called to provide the core configuration before building the SDK instance. Additional customization can be applied using [customizeWallet](customize-wallet.md) and [customizeManager](customize-manager.md) methods.

#### Return

This builder instance for method chaining

Example:

```kotlin
configure {
    wallet {
        configureDocumentManager(storageFile.absolutePath)
        configureLogging(level = Logger.LEVEL_INFO)
    }
    manager {
        organization = Organization.url("https://your-scytales-manager.url")
        signup {
            facetec { /* ... */}
            openIdConnect { /* ... */}
        }
    }
}
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| configure | Configuration lambda with [SdkConfig.Builder](../../-sdk-config/-builder/index.md) as receiver |

#### See also

| | |
|---|---|
| [SdkConfig](../../-sdk-config/index.md) | for available configuration options |
