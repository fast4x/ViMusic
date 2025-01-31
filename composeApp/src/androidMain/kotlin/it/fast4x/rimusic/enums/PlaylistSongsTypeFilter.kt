package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.AlbumsType.Favorites
import it.fast4x.rimusic.enums.AlbumsType.Library

enum class PlaylistSongsTypeFilter {
    All,
    OnlineSongs,
    Videos,
    Favorites,
    Local,
    Unmatched,
    Downloaded,
    Cached,
    Explicit
}