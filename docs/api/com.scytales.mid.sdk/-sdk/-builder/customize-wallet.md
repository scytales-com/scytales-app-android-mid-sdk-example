//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[Sdk](../index.md)/[Builder](index.md)/[customizeWallet](customize-wallet.md)

# customizeWallet

[Scytales MID SDK]\
abstract fun [customizeWallet](customize-wallet.md)(customize: &lt;Error class: unknown class&gt;.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Sdk.Builder](index.md)

Customize the Wallet with advanced component overrides.

Use this for advanced customization scenarios where you need to:

- 
   Replace default components with custom implementations
- 
   Configure storage or secure area providers
- 
   Set up custom logging or transaction logging
- 
   Override HTTP client factories
- 
   Configure custom reader trust stores

This is an optional customization step that allows you to fine-tune wallet behavior beyond what's available in the standard configuration. Use this only when you need to override default implementations.

#### Return

This builder instance for method chaining

Example:

```kotlin
customizeWallet {
    withLogger(customLogger)
    withStorage(customStorage)
    withSecureAreas(listOf(androidKeystoreSecureArea, cloudSecureArea))
    withKtorHttpClientFactory {
        HttpClient(OkHttp) {
            // custom HTTP client config
        }
    }
}
```

#### Parameters

Scytales MID SDK

| | |
|---|---|
| customize | Customization lambda with EudiWallet.Builder as receiver |

#### See also

| | |
|---|---|
| EudiWallet.Builder | for available customization options |
