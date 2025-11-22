//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[Sdk](index.md)/[getConfiguration](get-configuration.md)

# getConfiguration

[Scytales MID SDK]\
abstract fun [getConfiguration](get-configuration.md)(): [SdkConfig](../-sdk-config/index.md)

Returns the complete SDK configuration.

This method provides access to the full [SdkConfig](../-sdk-config/index.md) instance that contains both the EUDI Wallet configuration and the Scytales manager configuration used to initialize this SDK instance.

Use this method to inspect the current configuration settings for debugging, logging, or to create new SDK instances with similar configurations.

#### Return

The [SdkConfig](../-sdk-config/index.md) instance used to configure this SDK

Example:

```kotlin
val config = sdk.getConfiguration()
val walletConfig = config.walletConfig
val managerConfig = config.managerConfig
```

#### See also

| |
|---|
| [SdkConfig](../-sdk-config/index.md) |
