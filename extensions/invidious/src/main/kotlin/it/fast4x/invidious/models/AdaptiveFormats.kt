package it.fast4x.invidious.models

import kotlinx.serialization.Serializable

@Serializable
data class InvidiousResponse (
    val adaptiveFormats: List<AdaptiveFormat>? = null
) {
    val autoMaxQualityFormat: AdaptiveFormat?
        get() = adaptiveFormats?.sortedBy { it.bitrate }?.findLast {
            it.itag == "251" || it.itag == "141" ||
                    it.itag == "250" || it.itag == "140" ||
                    it.itag == "249" || it.itag == "139" || it.itag == "171"
        }

    val highestQualityFormat: AdaptiveFormat?
        get() = adaptiveFormats?.sortedBy { it.bitrate }?.findLast { it.itag == "251" || it.itag == "141" }

    val mediumQualityFormat: AdaptiveFormat?
        get() = adaptiveFormats?.sortedBy { it.bitrate }?.findLast { it.itag == "250" || it.itag == "140" }

    val lowestQualityFormat: AdaptiveFormat?
        get() = adaptiveFormats?.sortedBy { it.bitrate }?.findLast { it.itag == "249" || it.itag == "139" }
}

@Serializable
data class AdaptiveFormat (
    val init: String? = null,
    val index: String? = null,
    val bitrate: Long? = null,
    val url: String? = null,
    val itag: String? = null,
    val type: String? = null,
    val clen: String? = null,
    val lmt: String? = null,
    val projectionType: String? = null,
    val container: String? = null,
    val encoding: String? = null,
    val audioQuality: String? = null,
    val audioSampleRate: Long? = null,
    val audioChannels: Long? = null
) {
    val mimeType: String
        get() = type?.split( ";" )?.get(0)?.trim().toString()
    val codec: String
        get() = type?.split( ";" )?.get(1)?.trim().toString()
}