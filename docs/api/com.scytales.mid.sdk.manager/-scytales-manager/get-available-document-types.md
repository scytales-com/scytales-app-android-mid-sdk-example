//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager](../index.md)/[ScytalesManager](index.md)/[getAvailableDocumentTypes](get-available-document-types.md)

# getAvailableDocumentTypes

[Scytales MID SDK]\
abstract suspend fun [getAvailableDocumentTypes](get-available-document-types.md)(): &lt;Error class: unknown class&gt;&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AvailableDocumentType](../-available-document-type/index.md)&gt;&gt;

Retrieves a list of available document types that can be issued.

This method queries the system to determine which document types are currently available for issuance to the user. Document types may vary based on the organization configuration and issuer capabilities.

#### Return

A Result containing a list of [AvailableDocumentType](../-available-document-type/index.md) on success,     or an error if the operation fails.
