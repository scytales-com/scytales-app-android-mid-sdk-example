//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[SdkConfig](../index.md)/[Builder](index.md)/[build](build.md)

# build

[Scytales MID SDK]\
abstract suspend fun [build](build.md)(): [SdkConfig](../index.md)

Build the final [SdkConfig](../index.md) instance.

This method validates that all required configurations are provided and constructs the immutable configuration object. The manager configuration is mandatory and must be set before calling this method.

#### Return

Configured [SdkConfig](../index.md) instance

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if manager configuration is not set |
