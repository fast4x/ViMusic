package it.fast4x.rimusic.ui.components.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState

@UnstableApi
class DeleteHiddenSongsDialog private constructor(
    private val binder: PlayerServiceModern.Binder?,
    activeState: MutableState<Boolean>,
    menuState: MenuState
): DelSongDialog(binder, activeState, menuState) {

    companion object {
        @JvmStatic
        @Composable
        fun init() = DeleteHiddenSongsDialog(
            LocalPlayerServiceBinder.current,
            rememberSaveable { mutableStateOf( false ) },
            LocalMenuState.current
        )
    }

    override val messageId: Int = R.string.delete_hidden_songs_message
    override val iconId: Int = R.drawable.trash
    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.delete_hidden_songs )

    override fun onConfirm() {

            Database.asyncTransaction {
                menuState.hide()
                deleteHiddenSongs()
                cleanSongArtistMap()
                cleanSongAlbumMap()
                cleanSongPlaylistMap()
            }

        onDismiss()
    }
}