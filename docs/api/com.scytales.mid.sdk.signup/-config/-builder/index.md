//[Scytales MID SDK](../../../index.md)/[com.scytales.mid.sdk.signup](../../index.md)/[Config](../index.md)/[Builder](index.md)

# Builder

[Scytales MID SDK]\
class [Builder](index.md)

## Constructors

| | |
|---|---|
| [Builder](-builder.md) | [Scytales MID SDK]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [device](device.md) | [Scytales MID SDK]<br>var [device](device.md): [DeviceConfig](../../../com.scytales.mid.sdk.signup.config/-device-config/index.md)? |
| [facetec](facetec.md) | [Scytales MID SDK]<br>var [facetec](facetec.md): [FacetecConfig](../../../com.scytales.mid.sdk.signup.config/-facetec-config/index.md)? |
| [httpClientFactory](http-client-factory.md) | [Scytales MID SDK]<br>var [httpClientFactory](http-client-factory.md): () -&gt; &lt;Error class: unknown class&gt; |
| [issuerUrl](issuer-url.md) | [Scytales MID SDK]<br>var [issuerUrl](issuer-url.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)? |
| [openIdConnectUrl](open-id-connect-url.md) | [Scytales MID SDK]<br>var [openIdConnectUrl](open-id-connect-url.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)? |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [Scytales MID SDK]<br>fun [build](build.md)(): [Config](../index.md) |
| [device](device.md) | [Scytales MID SDK]<br>fun [device](device.md)(block: [DeviceConfig.Builder](../../../com.scytales.mid.sdk.signup.config/-device-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt;<br>fun [device](device.md)(publicKey: [PublicKey](https://developer.android.com/reference/kotlin/java/security/PublicKey.html), signer: [Signer](../../-signer/index.md)): &lt;Error class: unknown class&gt; |
| [facetec](facetec.md) | [Scytales MID SDK]<br>fun [facetec](facetec.md)(facetec: [FacetecConfig](../../../com.scytales.mid.sdk.signup.config/-facetec-config/index.md)): &lt;Error class: unknown class&gt;<br>fun [facetec](facetec.md)(block: [FacetecConfig.Builder](../../../com.scytales.mid.sdk.signup.config/-facetec-config/-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): &lt;Error class: unknown class&gt; |
| [httpClientFactory](http-client-factory.md) | [Scytales MID SDK]<br>fun [httpClientFactory](http-client-factory.md)(httpClientFactory: () -&gt; &lt;Error class: unknown class&gt;): &lt;Error class: unknown class&gt; |
| [issuerUrl](issuer-url.md) | [Scytales MID SDK]<br>fun [issuerUrl](issuer-url.md)(issuerUrl: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): &lt;Error class: unknown class&gt; |
