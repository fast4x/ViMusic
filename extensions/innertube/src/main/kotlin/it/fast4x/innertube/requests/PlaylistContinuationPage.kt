package it.fast4x.innertube.requests

import it.fast4x.innertube.Innertube


data class PlaylistContinuationPage(
    val songs: List<Innertube.SongItem>,
    val continuation: String?,
)
