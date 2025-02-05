package it.fast4x.rimusic.ui.components.themed

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import it.fast4x.innertube.YtMusic
import it.fast4x.innertube.models.NavigationEndpoint
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.PIPED_PREFIX
import it.fast4x.rimusic.R
import it.fast4x.rimusic.cleanPrefix
import it.fast4x.rimusic.context
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.service.MyDownloadHelper
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.ui.screens.settings.isYouTubeSyncEnabled
import it.fast4x.rimusic.utils.addToPipedPlaylist
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.forcePlay
import it.fast4x.rimusic.utils.getPipedSession
import it.fast4x.rimusic.utils.isPipedEnabledKey
import it.fast4x.rimusic.utils.menuStyleKey
import it.fast4x.rimusic.utils.rememberEqualizerLauncher
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.removeFromPipedPlaylist
import it.fast4x.rimusic.utils.seamlessPlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@ExperimentalTextApi
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun PlayerMenu(
    navController: NavController,
    binder: PlayerServiceModern.Binder,
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
    onClosePlayer: () -> Unit,
    onMatchingSong: (() -> Unit)? = null,
    disableScrollingText: Boolean
    ) {

    val menuStyle by rememberPreference(
        menuStyleKey,
        MenuStyle.List
    )

    //val context = LocalContext.current

    val launchEqualizer by rememberEqualizerLauncher(audioSessionId = { binder.player.audioSessionId })

    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    var isHiding by remember {
        mutableStateOf(false)
    }

    if (isHiding) {
        ConfirmationDialog(
            text = stringResource(R.string.update_song),
            onDismiss = { isHiding = false },
            onConfirm = {
                onDismiss()
                binder.cache.removeResource(mediaItem.mediaId)
                binder.downloadCache.removeResource(mediaItem.mediaId)
                Database.asyncTransaction {
                    resetTotalPlayTimeMs(mediaItem.mediaId)
                }
            }
        )
    }


    if (menuStyle == MenuStyle.Grid) {
        BaseMediaItemGridMenu(
            navController = navController,
            mediaItem = mediaItem,
            onDismiss = onDismiss,
            onStartRadio = {
                binder.stopRadio()
                binder.player.seamlessPlay(mediaItem)
                binder.setupRadio(NavigationEndpoint.Endpoint.Watch(videoId = mediaItem.mediaId))
            },
            onGoToEqualizer = launchEqualizer,
            /*
            onGoToEqualizer = {
                try {
                    activityResultLauncher.launch(
                        Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, binder.player.audioSessionId)
                            putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                        }
                    )
                } catch (e: ActivityNotFoundException) {
                    SmartMessage(context.resources.getString(R.string.info_not_find_application_audio), type = PopupType.Warning, context = context)
                }
            },
             */
            onHideFromDatabase = { isHiding = true },
            onClosePlayer = onClosePlayer,
            disableScrollingText = disableScrollingText
        )
    } else {
        BaseMediaItemMenu(
            navController = navController,
            mediaItem = mediaItem,
            onStartRadio = {
                binder.stopRadio()
                binder.player.seamlessPlay(mediaItem)
                binder.setupRadio(NavigationEndpoint.Endpoint.Watch(videoId = mediaItem.mediaId))
            },
            onGoToEqualizer = launchEqualizer,
            onShowSleepTimer = {},
            onHideFromDatabase = { isHiding = true },
            onDismiss = onDismiss,
            onAddToPreferites = {
                Database.asyncTransaction {
                    like(
                        mediaItem.mediaId,
                        System.currentTimeMillis()
                    )
                }
            },
            onClosePlayer = onClosePlayer,
            onMatchingSong = onMatchingSong,
            disableScrollingText = disableScrollingText
        )
    }

}


@ExperimentalTextApi
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun MiniPlayerMenu(
    navController: NavController,
    binder: PlayerServiceModern.Binder,
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
    onClosePlayer: () -> Unit,
    disableScrollingText: Boolean
) {

    val menuStyle by rememberPreference(
        menuStyleKey,
        MenuStyle.List
    )

    if (menuStyle == MenuStyle.Grid) {
        MiniMediaItemGridMenu(
            navController = navController,
            mediaItem = mediaItem,
            onGoToPlaylist = {
                onClosePlayer()
            },
            onAddToPreferites = {
                Database.asyncTransaction {
                    like(
                        mediaItem.mediaId,
                        System.currentTimeMillis()
                    )
                }
                MyDownloadHelper.autoDownloadWhenLiked(context(),mediaItem)
            },
            onDismiss = onDismiss,
            disableScrollingText = disableScrollingText
        )
    } else {
        MiniMediaItemMenu(
            navController = navController,
            mediaItem = mediaItem,
            onGoToPlaylist = {
                onClosePlayer()
            },
            onAddToPreferites = {
                Database.asyncTransaction {
                    like(
                        mediaItem.mediaId,
                        System.currentTimeMillis()
                    )
                }
                MyDownloadHelper.autoDownloadWhenLiked(context(),mediaItem)
            },
            onDismiss = onDismiss,
            disableScrollingText = disableScrollingText
        )
    }

}

@ExperimentalTextApi
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun AddToPlaylistPlayerMenu(
    navController: NavController,
    binder: PlayerServiceModern.Binder,
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
    onClosePlayer: () -> Unit,
) {
    val isPipedEnabled by rememberPreference(isPipedEnabledKey, false)
    val pipedSession = getPipedSession()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    AddToPlaylistItemMenu(
        navController = navController,
        mediaItem = mediaItem,
        onGoToPlaylist = {
            onClosePlayer()
        },
        onAddToPlaylist = { playlist, position ->
            Database.asyncTransaction {
                insert(mediaItem)
                insert(
                    SongPlaylistMap(
                        songId = mediaItem.mediaId,
                        playlistId = insert(playlist).takeIf { it != -1L } ?: playlist.id,
                        position = position
                    )
                )
            }

            if(isYouTubeSyncEnabled())
                CoroutineScope(Dispatchers.IO).launch {
                    playlist.browseId?.let { YtMusic.addToPlaylist(cleanPrefix(it), mediaItem.mediaId) }
                }
            if (playlist.name.startsWith(PIPED_PREFIX) && isPipedEnabled && pipedSession.token.isNotEmpty()) {
                Timber.d("BaseMediaItemMenu onAddToPlaylist mediaItem ${mediaItem.mediaId}")
                addToPipedPlaylist(
                    context = context,
                    coroutineScope = coroutineScope,
                    pipedSession = pipedSession.toApiSession(),
                    id = UUID.fromString(playlist.browseId),
                    videos = listOf(mediaItem.mediaId)
                )
            }
        },
        onRemoveFromPlaylist = { playlist ->
            Database.asyncTransaction {
                deleteSongFromPlaylist(mediaItem.mediaId,playlist.id)
                if (playlist.name.startsWith(PIPED_PREFIX) && isPipedEnabled && pipedSession.token.isNotEmpty()) {
                    Timber.d("MediaItemMenu InPlaylistMediaItemMenu onRemoveFromPlaylist browseId ${playlist.browseId}")
                    removeFromPipedPlaylist(
                        context = context,
                        coroutineScope = coroutineScope,
                        pipedSession = pipedSession.toApiSession(),
                        id = UUID.fromString(cleanPrefix(playlist.browseId ?: "")),positionInPlaylist(mediaItem.mediaId,playlist.id)
                    )
                }
            }
            if(isYouTubeSyncEnabled() && playlist.browseId != null && !playlist.name.startsWith(PIPED_PREFIX))
                CoroutineScope(Dispatchers.IO).launch {
                    playlist.browseId.let { YtMusic.removeFromPlaylist(
                        cleanPrefix(it), mediaItem.mediaId
                    ) }
                }
        },
        onDismiss = onDismiss,
    )
}