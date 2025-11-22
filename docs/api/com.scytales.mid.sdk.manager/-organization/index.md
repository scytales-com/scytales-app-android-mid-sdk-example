//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager](../index.md)/[Organization](index.md)

# Organization

sealed interface [Organization](index.md)

Represents an organization configuration for the Scytales Manager.

This sealed interface defines an organization by its Scytales Manager API URL. Use the companion object factory method [url](-companion/url.md) to create instances.

Example usage:

```kotlin
val org = Organization.url("https://your-scytales-manager.url")
```

#### See also

| |
|---|
| [Organization.ByUrl](-by-url/index.md) |
| [Organization.Companion.url](-companion/url.md) |

#### Inheritors

| |
|---|
| [ByUrl](-by-url/index.md) |

## Types

| Name | Summary |
|---|---|
| [ByUrl](-by-url/index.md) | [Scytales MID SDK]<br>value class [ByUrl](-by-url/index.md) : [Organization](index.md)<br>Organization identified by its Scytales Manager API URL. |
| [Companion](-companion/index.md) | [Scytales MID SDK]<br>object [Companion](-companion/index.md) |
