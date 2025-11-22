//[Scytales MID SDK](../index.md)/[com.scytales.mid.sdk.manager](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [AvailableDocumentType](-available-document-type/index.md) | [Scytales MID SDK]<br>data class [AvailableDocumentType](-available-document-type/index.md)(val format: &lt;Error class: unknown class&gt;, val template: &lt;Error class: unknown class&gt;)<br>Represents a document type that is available for issuance. |
| [Organization](-organization/index.md) | [Scytales MID SDK]<br>sealed interface [Organization](-organization/index.md)<br>Represents an organization configuration for the Scytales Manager. |
| [ScytalesManager](-scytales-manager/index.md) | [Scytales MID SDK]<br>interface [ScytalesManager](-scytales-manager/index.md)<br>Main interface for the Scytales Manager SDK. |
| [ScytalesManagerConfig](-scytales-manager-config/index.md) | [Scytales MID SDK]<br>data class [ScytalesManagerConfig](-scytales-manager-config/index.md)(val organization: [Organization](-organization/index.md), val signup: [SignupConfig](../com.scytales.mid.sdk.manager.signup/-signup-config/index.md))<br>Configuration for the Scytales Manager. |
| [ScytalesManagerConfigBuilder](-scytales-manager-config-builder/index.md) | [Scytales MID SDK]<br>class [ScytalesManagerConfigBuilder](-scytales-manager-config-builder/index.md)<br>Builder class for constructing [ScytalesManagerConfig](-scytales-manager-config/index.md) instances. |

## Functions

| Name | Summary |
|---|---|
| [scytalesManagerConfig](scytales-manager-config.md) | [Scytales MID SDK]<br>fun [scytalesManagerConfig](scytales-manager-config.md)(block: [ScytalesManagerConfigBuilder](-scytales-manager-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [ScytalesManagerConfig](-scytales-manager-config/index.md)<br>Creates a [ScytalesManagerConfig](-scytales-manager-config/index.md) using a builder DSL. |
