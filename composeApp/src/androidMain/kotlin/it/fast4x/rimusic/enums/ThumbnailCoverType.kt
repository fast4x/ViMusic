package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R

enum class ThumbnailCoverType {
    Vinyl,
    CD,
    CDwithCover;

    val textName: String
        @Composable
        get() = when (this) {
            Vinyl -> stringResource(R.string.cover_type_vinyl)
            CD -> stringResource(R.string.cover_type_cd)
            CDwithCover -> stringResource(R.string.cover_type_cd_with_cover)
        }
}