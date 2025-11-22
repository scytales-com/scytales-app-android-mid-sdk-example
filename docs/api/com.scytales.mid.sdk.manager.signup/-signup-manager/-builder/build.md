//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.manager.signup](../../index.md)/[SignupManager](../index.md)/[Builder](index.md)/[build](build.md)

# build

[Scytales MID SDK]\
abstract suspend fun [build](build.md)(): [SignupManager](../index.md)

Builds and returns a configured [SignupManager](../index.md) instance.

This method validates that all required configurations are set and creates a fully initialized signup manager ready for document issuance operations.

#### Return

A configured [SignupManager](../index.md) instance.

#### Throws

| | |
|---|---|
| [IllegalArgumentException](https://developer.android.com/reference/kotlin/java/lang/IllegalArgumentException.html) | if required configurations are missing. |
| [IllegalStateException](https://developer.android.com/reference/kotlin/java/lang/IllegalStateException.html) | if the builder is in an invalid state. |
