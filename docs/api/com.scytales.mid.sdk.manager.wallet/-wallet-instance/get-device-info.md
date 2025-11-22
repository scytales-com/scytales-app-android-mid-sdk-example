//[Scytales MID SDK](../../index.md)/[com.scytales.mid.sdk.manager.wallet](../index.md)/[WalletInstance](index.md)/[getDeviceInfo](get-device-info.md)

# getDeviceInfo

[Scytales MID SDK]\
abstract suspend fun [getDeviceInfo](get-device-info.md)(): [DeviceInfo](../-device-info/index.md)

Retrieves the device information and metadata.

This provides contextual information about the device, OS, and app version. While some fields may change (e.g., OS version after an update), the device should remain identifiable through the stable wallet ID.

#### Return

The [DeviceInfo](../-device-info/index.md) containing device metadata.
