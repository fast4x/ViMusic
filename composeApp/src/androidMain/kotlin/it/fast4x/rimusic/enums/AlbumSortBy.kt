package it.fast4x.rimusic.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import it.fast4x.rimusic.R

enum class AlbumSortBy(
    @StringRes val textId: Int,
    @DrawableRes val iconId: Int
): MenuTitle, Drawable {

    Title( R.string.sort_album, R.drawable.text ),

    Year( R.string.sort_album_year, R.drawable.calendar ),

    DateAdded( R.string.sort_date_added, R.drawable.time ),

    Artist( R.string.sort_artist, R.drawable.artist ),

    Songs( R.string.sort_songs_number, R.drawable.medical ),

    Duration( R.string.sort_duration, R.drawable.time );

    override val titleId: Int
        get() = this.textId

    override val icon: Painter
        @Composable
        get() = painterResource( this.iconId )
}
