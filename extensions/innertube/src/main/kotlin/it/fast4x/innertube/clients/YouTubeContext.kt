package it.fast4x.innertube.clients

import kotlinx.serialization.Serializable

@Serializable
data class YouTubeContext(
    val client: Client,
    val thirdParty: ThirdParty? = null,
) {
    @Serializable
    data class Client(
        val clientName: String,
        val clientVersion: String,
        val gl: String,
        val hl: String,
        val visitorData: String?,
    )

    @Serializable
    data class ThirdParty(
        val embedUrl: String,
    )
}
