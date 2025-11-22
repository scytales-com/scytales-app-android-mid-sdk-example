//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[Sdk](../index.md)/[Builder](index.md)/[customizeManager](customize-manager.md)

# customizeManager

[Scytales MID SDK]\
abstract fun [customizeManager](customize-manager.md)(customize: [ScytalesManager.Builder](../../../com.scytales.mid.sdk.manager/-scytales-manager/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](index.md)

Customize the Scytales manager with advanced settings.

Use this for advanced customization scenarios where you need to:

- 
   Configure custom document creation settings
- 
   Override wallet instance providers
- 
   Set up custom biometric enrollment flows

This is an optional customization step that allows you to fine-tune manager behavior beyond what's available in the standard configuration. Use this only when you need to override default implementations.

#### Return

This builder instance for method chaining

Example:

```kotlin
customizeManager {
    createDocumentSettings(customCreateSettings)
    walletInstanceProvider(customProvider)
}
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| customize | Customization lambda with [ScytalesManager.Builder](../../../com.scytales.mid.sdk.manager/-scytales-manager/-builder/index.md) as receiver |

#### See also

| | |
|---|---|
| [ScytalesManager.Builder](../../../com.scytales.mid.sdk.manager/-scytales-manager/-builder/index.md) | for available customization options |
