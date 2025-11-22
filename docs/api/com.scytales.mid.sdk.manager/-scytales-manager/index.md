//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager](../index.md)/[ScytalesManager](index.md)

# ScytalesManager

interface [ScytalesManager](index.md)

Main interface for the Scytales Manager SDK.

This interface provides core functionality for managing digital identity documents, including retrieving available document types and creating signup managers for user enrollment flows.

#### See also

| |
|---|
| ScytalesManagerBuilder |
| [SignupManager](../../com.scytales.mid.sdk.manager.signup/-signup-manager/index.md) |
| [AvailableDocumentType](../-available-document-type/index.md) |

#### Inheritors

| |
|---|
| [Sdk](../../com.scytales.mid.sdk/-sdk/index.md) |

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [Scytales MID SDK]<br>interface [Builder](-builder/index.md)<br>Builder interface for constructing [ScytalesManager](index.md) instances. |

## Functions

| Name | Summary |
|---|---|
| [createSignManager](create-sign-manager.md) | [Scytales MID SDK]<br>abstract suspend fun [createSignManager](create-sign-manager.md)(): [SignupManager](../../com.scytales.mid.sdk.manager.signup/-signup-manager/index.md)<br>Creates a signup manager for handling user enrollment flows. |
| [getAvailableDocumentTypes](get-available-document-types.md) | [Scytales MID SDK]<br>abstract suspend fun [getAvailableDocumentTypes](get-available-document-types.md)(): &lt;Error class: unknown class&gt;&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AvailableDocumentType](../-available-document-type/index.md)&gt;&gt;<br>Retrieves a list of available document types that can be issued. |
