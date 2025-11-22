//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkConfigBuilder](index.md)/[build](build.md)

# build

[Scytales MID SDK]\
open suspend override fun [build](build.md)(): [SdkConfig](../-sdk-config/index.md)

Build and validate the final [SdkConfig](../-sdk-config/index.md) instance.

This method performs validation to ensure all required configurations are present and then constructs an immutable [SdkConfig](../-sdk-config/index.md) object combining both the EUDI Wallet configuration and the Scytales manager configuration.

Validation checks:

- 
   Manager configuration must be set (required for Scytales features)

The wallet configuration is always present as it has a default value, but the manager configuration must be explicitly provided.

#### Return

Fully configured and validated [SdkConfig](../-sdk-config/index.md) instance

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if manager configuration has not been set<br>Example:<br>```kotlin val config = sdkConfig {     wallet { /* ... */}     manager { /* ... */} }.build() ``` |
