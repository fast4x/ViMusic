package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.utils.playShuffledSongs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import it.fast4x.rimusic.appContext
import kotlin.coroutines.cancellation.CancellationException

@UnstableApi
class SongsShuffle private constructor(
    private val binder: PlayerServiceModern.Binder?,
    private val songs: () -> Flow<List<MediaItem>>
): MenuIcon, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init( songs: () -> Flow<List<MediaItem>> ) =
            SongsShuffle( LocalPlayerServiceBinder.current, songs )
    }

    override val iconId: Int = R.drawable.shuffle
    override val messageId: Int = R.string.shuffle
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        CoroutineScope( Dispatchers.IO ).launch {
            songs().collect {
                playShuffledSongs( it, appContext(), binder )
                throw CancellationException()
            }
        }
    }
}