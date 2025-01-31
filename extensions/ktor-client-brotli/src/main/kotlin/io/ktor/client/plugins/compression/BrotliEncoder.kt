package io.ktor.client.plugins.compression

import io.ktor.util.ContentEncoder
import io.ktor.util.Encoder
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import org.brotli.dec.BrotliInputStream
import kotlin.coroutines.CoroutineContext

object BrotliEncoder : ContentEncoder, Encoder by BrotliEncoder {
    override val name = "br"
    override fun decode(source: ByteReadChannel, coroutineContext: CoroutineContext): ByteReadChannel {
        return BrotliInputStream(source.toInputStream()).toByteReadChannel()
    }

    override fun encode(source: ByteReadChannel, coroutineContext: CoroutineContext): ByteReadChannel {
        error("BrotliOutputStream not available (<https://github.com/google/brotli/issues/715>)")
    }

    override fun encode(source: ByteWriteChannel, coroutineContext: CoroutineContext): ByteWriteChannel {
        error("BrotliOutputStream not available (<https://github.com/google/brotli/issues/715>)")
    }
}

fun ContentEncodingConfig.brotli(quality: Float? = null) {
    customEncoder(BrotliEncoder, quality)
}

