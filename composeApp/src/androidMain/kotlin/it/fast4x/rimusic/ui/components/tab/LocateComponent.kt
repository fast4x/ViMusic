package it.fast4x.rimusic.ui.components.tab

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.PopupType
import it.fast4x.rimusic.service.modern.PlayerServiceModern
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import kotlinx.coroutines.runBlocking
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.DynamicColor
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon

@UnstableApi
class LocateComponent private constructor(
    private val binder: PlayerServiceModern.Binder?,
    private val scrollableState: ScrollableState,
    private val positionState: MutableState<Int>,
    private val songs: () -> List<MediaItem>
): MenuIcon, DynamicColor, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init(
            scrollableState: ScrollableState,
            songs: () -> List<MediaItem>
        ) =
            LocateComponent(
                LocalPlayerServiceBinder.current,
                scrollableState,
                remember { mutableIntStateOf(-1) },
                songs
            )
    }

    var position: Int = positionState.value
        set(value) {
            positionState.value = value
            field = value
        }
    override var isFirstColor: Boolean = songs().isNotEmpty() && binder?.player?.currentMediaItem != null
    override val iconId: Int = R.drawable.locate
    override val messageId: Int = R.string.info_find_the_song_that_is_playing
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        binder?.player
              ?.currentMediaItem
              ?.let { mediaItem ->
                  println("LocateComponent.onShortClick songs ${songs().size} -> mediaItem ${mediaItem.mediaId}")
                  songs().map { it.mediaId }.indexOf( mediaItem.mediaId ).let {
                      if( it == -1 )      // Playing song isn't inside [songs()]
                          SmartMessage(
                              // TODO Add this string to strings.xml
                              message = "Couldn't find playing song on current list",
                              context = appContext(),
                              type = PopupType.Warning
                          )
                      else
                          runBlocking {
                              if( scrollableState is LazyListState )
                                  scrollableState.scrollToItem( it )
                              else if( scrollableState is LazyGridState )
                                  scrollableState.scrollToItem( it )

                              position = it
                          }
                  }
              }
    }
}