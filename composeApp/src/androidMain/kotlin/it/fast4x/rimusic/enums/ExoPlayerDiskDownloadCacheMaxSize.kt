package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R
import me.knighthat.enums.TextView

enum class ExoPlayerDiskDownloadCacheMaxSize(
    val megaBytes: Int
):TextView {

    Disabled( 1 ),
    `32MB`( 32 ),
    `512MB`( 512 ),
    `1GB`( 1024 ),
    `2GB`( 2048 ),
    `4GB`( 4096 ),
    `8GB`( 8192 ),
    Unlimited( 0 );

    val bytes: Long
        get() = this.megaBytes * 1000 * 1000L

    override val text: String
        @Composable
        get() = when( this ) {
            Disabled -> stringResource( R.string.turn_off )
            Unlimited -> stringResource( R.string.unlimited )
            else -> this.name
        }
}
