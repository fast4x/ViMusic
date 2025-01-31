package it.fast4x.rimusic.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import it.fast4x.rimusic.R

enum class ArtistSortBy(
    @StringRes val textId: Int,
    @DrawableRes val iconId: Int
): MenuTitle, Drawable {

    Name( R.string.sort_artist, R.drawable.text ),

    DateAdded( R.string.sort_date_added, R.drawable.time );

    override val titleId: Int
        get() = this.textId

    override val icon: Painter
        @Composable
        get() = painterResource( this.iconId )
}
