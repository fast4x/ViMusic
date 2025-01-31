package it.fast4x.rimusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult

suspend fun getBitmapFromUrl(context: Context, url: String): Bitmap {
    val loading = context.imageLoader
    val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
    val result = loading.execute(request)
    if(result is ErrorResult) {
        throw result.throwable
    }
    val drawable = (result as SuccessResult).drawable
    return (drawable as BitmapDrawable).bitmap
}


