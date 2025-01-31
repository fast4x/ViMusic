package it.fast4x.rimusic.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.PowerManager
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.offline.DownloadService.sendAddDownload
import androidx.media3.exoplayer.offline.DownloadService.sendRemoveDownload

inline fun <reified T> Context.intent(): Intent =
    Intent(this, T::class.java)

inline fun <reified T : BroadcastReceiver> Context.broadCastPendingIntent(
    requestCode: Int = 0,
    flags: Int = if (isAtLeastAndroid6) PendingIntent.FLAG_IMMUTABLE else 0,
): PendingIntent =
    PendingIntent.getBroadcast(this, requestCode, intent<T>(), flags)

inline fun <reified T : Activity> Context.activityPendingIntent(
    requestCode: Int = 0,
    flags: Int = 0,
    block: Intent.() -> Unit = {},
): PendingIntent =
    PendingIntent.getActivity(
        this,
        requestCode,
        intent<T>().apply(block),
        (if (isAtLeastAndroid6) PendingIntent.FLAG_IMMUTABLE else 0) or flags
    )

val Context.isIgnoringBatteryOptimizations: Boolean
    get() = if (isAtLeastAndroid6) {
        getSystemService<PowerManager>()?.isIgnoringBatteryOptimizations(packageName) ?: true
    } else {
        true
    }

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Context.toastLong(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.hasPermission(permission: String) = ContextCompat.checkSelfPermission(
    applicationContext,
    permission
) == PackageManager.PERMISSION_GRANTED

/*
fun launchYouTubeMusic(
    context: Context,
    endpoint: String,
    tryWithoutBrowser: Boolean = true
): Boolean {
    return try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://music.youtube.com/${endpoint.dropWhile { it == '/' }}")
        ).apply {
            if (tryWithoutBrowser && isAtLeastAndroid11) {
                flags = Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            }
        }
        intent.`package` =
            context.applicationContext.packageManager.queryIntentActivities(intent, 0)
                .firstOrNull {
                    it?.activityInfo?.packageName != null &&
                            BuildConfig.APPLICATION_ID !in it.activityInfo.packageName
                }?.activityInfo?.packageName
                ?: return false
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        if (tryWithoutBrowser) launchYouTubeMusic(
            context = context,
            endpoint = endpoint,
            tryWithoutBrowser = false
        ) else false
    }
}
 */

@OptIn(UnstableApi::class)
inline fun <reified T : DownloadService> Context.download(request: DownloadRequest) = runCatching {
    sendAddDownload(
        /* context         = */ this,
        /* clazz           = */ T::class.java,
        /* downloadRequest = */ request,
        /* foreground      = */ true
    )
}.recoverCatching {
    sendAddDownload(
        /* context         = */ this,
        /* clazz           = */ T::class.java,
        /* downloadRequest = */ request,
        /* foreground      = */ false
    )
}

@OptIn(UnstableApi::class)
inline fun <reified T : DownloadService> Context.removeDownload(mediaId: String) = runCatching {
    sendRemoveDownload(
        /* context         = */ this,
        /* clazz           = */ T::class.java,
        /* id              = */ mediaId,
        /* foreground      = */ false
    )
}

fun Context.isConnectionMetered(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return manager.isActiveNetworkMetered
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    error("Should be called in the context of an Activity")
}