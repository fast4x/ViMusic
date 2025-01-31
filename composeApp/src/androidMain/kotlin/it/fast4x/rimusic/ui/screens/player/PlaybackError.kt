package it.fast4x.rimusic.ui.screens.player

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.service.FakeException
import it.fast4x.rimusic.service.LoginRequiredException
import it.fast4x.rimusic.service.NoInternetException
import it.fast4x.rimusic.service.PlayableFormatNonSupported
import it.fast4x.rimusic.service.PlayableFormatNotFoundException
import it.fast4x.rimusic.service.TimeoutException
import it.fast4x.rimusic.service.UnknownException
import it.fast4x.rimusic.service.UnplayableException
import it.fast4x.rimusic.service.VideoIdMismatchException
import it.fast4x.rimusic.service.isLocal
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.styling.PureBlackColorPalette
import it.fast4x.rimusic.utils.center
import it.fast4x.rimusic.utils.color
import it.fast4x.rimusic.utils.currentWindow
import it.fast4x.rimusic.utils.medium
import it.fast4x.rimusic.typography
import timber.log.Timber
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

@OptIn(UnstableApi::class)
@Composable
fun PlayerError(error: PlaybackException) {
    val context = LocalContext.current
    val binder = LocalPlayerServiceBinder.current
    //val player = binder?.player ?: return

    val localMusicFileNotFoundError = stringResource(R.string.error_local_music_not_found)
    val networkerror = stringResource(R.string.error_a_network_error_has_occurred)
    val notfindplayableaudioformaterror =
        stringResource(R.string.error_couldn_t_find_a_playable_audio_format)
    //val originalvideodeletederror =
    //    stringResource(R.string.error_the_original_video_source_of_this_song_has_been_deleted)
    val unplayableerror = stringResource(R.string.error_media_cannot_be_played)
    //val songnotplayabledueserverrestrictionerror =
    //    stringResource(R.string.error_this_song_cannot_be_played_due_to_server_restrictions)
    val loginrequirederror = stringResource(R.string.login_required_to_play_this_media)
    val videoidmismatcherror =
        stringResource(R.string.error_the_returned_video_id_doesn_t_match_the_requested_one)
    //val unknownplaybackerror =
    //    stringResource(R.string.error_an_unknown_playback_error_has_occurred)

    //val unknownerror = stringResource(R.string.error_unknown)
    val unknownerror = stringResource(R.string.error_media_cannot_be_played)
    val nointerneterror = stringResource(R.string.error_no_internet)
    val timeouterror = stringResource(R.string.error_timeout)

    val formatUnsupported = stringResource(R.string.error_file_unsupported_format)

    var errorCounter = 0
    errorCounter = errorCounter.plus(1)

    if (errorCounter < 2) {
        Timber.e("Playback error: ${error.cause?.cause}")
        SmartMessage(
            if (binder?.player?.currentWindow?.mediaItem?.isLocal == true) localMusicFileNotFoundError
            else when (error.cause?.cause) {
                is UnresolvedAddressException, is UnknownHostException -> networkerror
                is PlayableFormatNotFoundException -> notfindplayableaudioformaterror
                is UnplayableException -> unplayableerror
                is LoginRequiredException -> loginrequirederror
                is VideoIdMismatchException -> videoidmismatcherror
                is PlayableFormatNonSupported -> formatUnsupported
                is NoInternetException -> nointerneterror
                is TimeoutException -> timeouterror
                is UnknownException -> unknownerror
                is FakeException -> unknownerror
                else -> unknownerror
            }, PopupType.Warning, context = context
        )
    }

}

@Composable
fun PlaybackError(
    isDisplayed: Boolean,
    messageProvider: () -> String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box {
        AnimatedVisibility(
            visible = isDisplayed,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Spacer(
                modifier = modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onDismiss()
                            }
                        )
                    }
                    .fillMaxSize()
                    .background(Color.Black.copy(0.8f))
            )
        }

        AnimatedVisibility(
            visible = isDisplayed,
            enter = slideInVertically { -it },
            exit = slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            BasicText(
                text = remember { messageProvider() },
                style = typography().xs.center.medium.color(PureBlackColorPalette.text),
                modifier = Modifier
                    .background(Color.Black.copy(0.4f))
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

