//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager](../index.md)/[AvailableDocumentType](index.md)

# AvailableDocumentType

data class [AvailableDocumentType](index.md)(val format: &lt;Error class: unknown class&gt;, val template: &lt;Error class: unknown class&gt;)

Represents a document type that is available for issuance.

This data class combines the document format specification with its associated template metadata, providing all necessary information to issue a document of this type to a user.

Instances of this class are typically retrieved via [ScytalesManager.getAvailableDocumentTypes](../-scytales-manager/get-available-document-types.md) and used during the document issuance flow.

#### See also

| |
|---|
| [ScytalesManager.getAvailableDocumentTypes](../-scytales-manager/get-available-document-types.md) |
| DocumentFormat |
| DocumentTemplate |

## Constructors

| | |
|---|---|
| [AvailableDocumentType](-available-document-type.md) | [Scytales MID SDK]<br>constructor(format: &lt;Error class: unknown class&gt;, template: &lt;Error class: unknown class&gt;) |

## Properties

| Name | Summary |
|---|---|
| [format](format.md) | [Scytales MID SDK]<br>val [format](format.md): &lt;Error class: unknown class&gt;<br>The document format specification (e.g., mDoc, SD-JWT VC) that defines     how the document is structured and encoded. |
| [template](template.md) | [Scytales MID SDK]<br>val [template](template.md): &lt;Error class: unknown class&gt;<br>The document template containing metadata and configuration for     this specific document type, including claims, display information,     and issuance requirements. |
