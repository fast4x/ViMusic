package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.service.isLocal
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.utils.manageDownload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import it.fast4x.rimusic.appContext

@UnstableApi
class DelAllDownloadedDialog private constructor(
    private val binder: PlayerServiceModern.Binder?,
    private val activeState: MutableState<Boolean>,
    private val songs: () -> List<MediaItem>
): ConfirmDialog, Descriptive, MenuIcon {

    companion object {
        @JvmStatic
        @Composable
        fun init( songs: () -> List<MediaItem> ) =
            DelAllDownloadedDialog(
                LocalPlayerServiceBinder.current,
                rememberSaveable { mutableStateOf( false ) },
                songs
            )
    }

    override var isActive: Boolean = activeState.value
        set(value) {
            activeState.value = value
            field = value
        }
    override val messageId: Int = R.string.info_remove_all_downloaded_songs
    override val iconId: Int = R.drawable.download
    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.do_you_really_want_to_delete_download)
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() = super.onShortClick()

    override fun onConfirm() {
        songs().forEach {
            // binder has to be non-null for remove from cache to work
            if(binder == null) return
            binder.cache.removeResource(it.mediaId)

            CoroutineScope(Dispatchers.IO).launch {
                Database.deleteFormat( it.mediaId )
            }

            if (!it.isLocal)
                manageDownload(
                    context = appContext(),
                    mediaItem = it,
                    downloadState = true
                )
        }

        onDismiss()
    }
}