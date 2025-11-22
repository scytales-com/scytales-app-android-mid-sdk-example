//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.manager](../../index.md)/[Organization](../index.md)/[Companion](index.md)/[url](url.md)

# url

[Scytales MID SDK]\
fun [url](url.md)(url: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): [Organization.ByUrl](../-by-url/index.md)

Creates an [Organization.ByUrl](../-by-url/index.md) instance using the provided Scytales Manager API URL.

The URL will be normalized by removing any trailing slash. It must start with `http://` or `https://` and be a valid URL format.

#### Return

An [Organization.ByUrl](../-by-url/index.md) instance with the normalized URL.

#### Parameters

Scytales MID SDK

| | |
|---|---|
| url | The Scytales Manager API URL for the organization. |

#### Throws

| | |
|---|---|
| [IllegalArgumentException](https://developer.android.com/reference/kotlin/java/lang/IllegalArgumentException.html) | if the URL is blank, doesn't start with http:// or https://,     or is not a valid URL format. |
