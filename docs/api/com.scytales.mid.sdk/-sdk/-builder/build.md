//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[Sdk](../index.md)/[Builder](index.md)/[build](build.md)

# build

[Scytales MID SDK]\
abstract suspend fun [build](build.md)(): [Sdk](../index.md)

Build and initialize the SDK instance.

This method validates all configurations, initializes both the Wallet and Scytales manager components, and returns a fully functional [Sdk](../index.md) instance.

The build process:

1. 
   Validates that core configuration has been provided via [configure](configure.md)
2. 
   Constructs the Wallet with provided configuration and customizations
3. 
   Constructs the Scytales manager with provided configuration and customizations
4. 
   Combines both into a unified SDK instance

This method is suspend because it may perform initialization operations that require asynchronous processing, such as database setup or network calls.

#### Return

Fully initialized [Sdk](../index.md) instance ready for use

#### Throws

| | |
|---|---|
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if required configuration is missing<br>Example:<br>```kotlin val sdk = SdkBuilder(context)     .configure { /* ... */}     .customizeWallet { /* ... */}     .customizeManager { /* ... */}     .build() ``` |
