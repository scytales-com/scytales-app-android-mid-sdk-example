//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkBuilder](index.md)/[build](build.md)

# build

[Scytales MID SDK]\
open suspend override fun [build](build.md)(): [Sdk](../-sdk/index.md)

Builds and returns a configured [Sdk](../-sdk/index.md) instance.

This method performs the following steps:

1. 
   Validates that a license configuration has been provided
2. 
   Evaluates the license against the provided context
3. 
   Delegates to the parent builder to complete SDK construction

This is a suspending function because license evaluation may involve asynchronous operations such as network requests or cryptographic validation.

#### Return

A fully configured and initialized [Sdk](../-sdk/index.md) instance

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if no license configuration has been set |
| [Exception](https://developer.android.com/reference/kotlin/java/lang/Exception.html) | if license evaluation fails<br>Example:<br>```kotlin lifecycleScope.launch {     val sdk = SdkBuilder(context)         .license(licenseConfig)         .build() } ``` |
