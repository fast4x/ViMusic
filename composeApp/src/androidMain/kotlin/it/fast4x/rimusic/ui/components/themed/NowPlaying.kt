package it.fast4x.rimusic.ui.components.themed

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import it.fast4x.rimusic.ui.components.MusicAnimation
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.onOverlay
import it.fast4x.rimusic.colorPalette

@OptIn(UnstableApi::class)
@Composable
fun NowPlayingSongIndicator (
    mediaId: String,
    player: ExoPlayer?,
    containerSize: Dp = Dimensions.thumbnails.song
) {

    if (player?.currentMediaItem?.mediaId != mediaId) return

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
//            .background(
//                color = Color.Black.copy(alpha = 0.25f),
//                shape = thumbnailShape()
//            )
            .size(containerSize)
    ) {
            MusicAnimation(
                color = colorPalette().onOverlay,
                modifier = Modifier
                    .height(containerSize / 2)
            )
    }

}