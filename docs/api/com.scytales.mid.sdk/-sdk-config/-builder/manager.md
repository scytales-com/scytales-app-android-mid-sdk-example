//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk](../../index.md)/[SdkConfig](../index.md)/[Builder](index.md)/[manager](manager.md)

# manager

[Scytales MID SDK]\
abstract fun [manager](manager.md)(block: [ScytalesManagerConfigBuilder](../../../com.scytales.mid.sdk.manager/-scytales-manager-config-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [SdkConfig.Builder](index.md)

Configure Scytales manager settings using a DSL block.

This method provides access to proprietary Scytales features including:

- 
   Organization configuration
- 
   FaceTec biometric enrollment settings
- 
   OpenID Connect configuration for document issuance
- 
   Custom signup workflows with biometric enrollment

This configuration is required for using Scytales-specific features such as biometric enrollment and custom document issuance workflows.

#### Return

This builder instance for method chaining

#### Parameters

Scytales MID SDK

| | |
|---|---|
| block | Configuration lambda with [ScytalesManagerConfigBuilder](../../../com.scytales.mid.sdk.manager/-scytales-manager-config-builder/index.md) as receiver |

[Scytales MID SDK]\
abstract fun [manager](manager.md)(config: [ScytalesManagerConfig](../../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)): [SdkConfig.Builder](index.md)

Set Scytales manager configuration directly.

Use this method if you have already constructed a [ScytalesManagerConfig](../../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md) instance elsewhere and want to use it directly instead of configuring it via the DSL.

#### Return

This builder instance for method chaining

#### Parameters

Scytales MID SDK

| | |
|---|---|
| config | Pre-configured [ScytalesManagerConfig](../../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md) instance |
