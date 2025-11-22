//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk](../index.md)/[SdkConfigBuilder](index.md)/[managerConfig](manager-config.md)

# managerConfig

[Scytales MID SDK]\
var [managerConfig](manager-config.md): [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md)?

Configuration for Scytales manager proprietary features.

This property holds the [ScytalesManagerConfig](../../com.scytales.mid.sdk.manager/-scytales-manager-config/index.md) instance that configures Scytales-specific functionality including organization settings, FaceTec biometric enrollment, OpenID Connect configuration, and custom signup workflows for document issuance.

This configuration is mandatory and must be set via the `manager { }` DSL block before calling [build](build.md). The configuration is accessed through the manager DSL methods and cannot be set directly from outside the builder.
