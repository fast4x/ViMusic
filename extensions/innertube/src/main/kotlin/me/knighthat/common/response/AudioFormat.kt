package me.knighthat.common.response

interface AudioFormat: Comparable<AudioFormat> {

    val itag: UShort
    val url: String?
    val mimeType: String
    val codec: String
    val bitrate: UInt

    /**
     * Using bitrate as the value to compare between objects
     *
     * @param other format to compare to
     */
    override fun compareTo( other: AudioFormat ): Int =
        this.bitrate.compareTo( other.bitrate )
}