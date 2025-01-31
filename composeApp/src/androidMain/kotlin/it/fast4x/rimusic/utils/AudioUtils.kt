package it.fast4x.rimusic.utils

import android.animation.ValueAnimator
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import androidx.media3.exoplayer.ExoPlayer
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.animation.doOnEnd
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.utils.playbackVolumeKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import kotlin.time.Duration.Companion.seconds

var volume = 0f

fun audioFadeOut(player: ExoPlayer, duration: Int, context: Context) {
    val deviceVolume = getDeviceVolume(context);
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        var time = duration
        var volume = 0.0f

        val myFadeOutRunnable: Runnable = object : Runnable {
            override fun run() {
                if (time > 0) {
                    time -= 100
                    volume = (deviceVolume * time) / duration
                    player.volume = volume
                    //println("mediaItem audioFadeOut: volume $volume $time")
                    handler.postDelayed(this, 100)
                }
            }
        }
        handler.postDelayed(myFadeOutRunnable, 100)

    }, 100);
}

fun audioFadeIn(player: ExoPlayer, duration: Int, context: Context) {
    //val deviceVolume = getDeviceVolume(context);
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        var time = 0
        var volume = 0.0f

        val myFadeInRunnable: Runnable = object : Runnable {
            override fun run() {
                if (time < duration) {
                    time += 100
                    //volume = (deviceVolume * time) / duration
                    //volume = (time.toFloat() / duration)
                    volume = (time.toFloat() / duration).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                    //volume = ((deviceVolume * time) / duration).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                    //player.volume = volume
                    if (player.volume < volume) {
                        player.volume = volume
                        //println("mediaItem audioFadeIn: player.volume ${player.volume} volume $volume time $time")
                    }

                    handler.postDelayed(this, 100)
                }
            }
        }
        handler.postDelayed(myFadeInRunnable, 100)

    }, 100);
}

fun getDeviceVolume(context: Context): Float {
    val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
    val volumeLevel: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    val maxVolume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    return volumeLevel.toFloat() / maxVolume
}

fun setDeviceVolume(context: Context, volume: Float) {
    val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (volume * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toInt(), 0)
}

@Composable
@OptIn(UnstableApi::class)
fun MedleyMode(binder: PlayerServiceModern.Binder?, seconds: Int) {
    if (seconds == 0) return
    if (binder != null) {
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                while (isActive) {
                    delay(1.seconds * seconds)
                    withContext(Dispatchers.Main) {
                        if (binder.player.isPlaying)
                            binder.player.playNext()
                    }
                }
            }
        }
    }
}

fun startFadeAnimator(
    player: ExoPlayer,
    duration: Int,
    fadeIn: Boolean, /* fadeIn -> true  fadeOut -> false*/
    callback: Runnable? = null, /* Code to run when Animator Ends*/
) {
    //println("mediaItem startFadeAnimator: fadeIn $fadeIn duration $duration callback $callback")
    val fadeDuration = duration.toLong()
    if (fadeDuration == 0L) {
        callback?.run()
        return
    }
    // Using global volume as max/min value
    // so fading wouldn't make the audio louder
    val startValue = if (fadeIn) 0f else player.getGlobalVolume()
    val endValue = if (fadeIn) player.getGlobalVolume() else 0f
    val animator = ValueAnimator.ofFloat(startValue, endValue)
    animator.duration = fadeDuration
    if (fadeIn) player.volume = startValue
    animator.addUpdateListener { animation: ValueAnimator ->
            player.volume = animation.animatedValue as Float
    }
    animator.doOnEnd {
        callback?.run()
        player.restoreGlobalVolume()
    }
    animator.start()
}