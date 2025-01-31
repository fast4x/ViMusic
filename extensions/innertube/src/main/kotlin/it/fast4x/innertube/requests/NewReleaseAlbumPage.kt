package it.fast4x.innertube.requests

import it.fast4x.innertube.Innertube
import it.fast4x.innertube.Innertube.getBestQuality
import it.fast4x.innertube.models.MusicTwoRowItemRenderer
import it.fast4x.innertube.models.oddElements
import it.fast4x.innertube.models.splitBySeparator

object NewReleaseAlbumPage {
    fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): Innertube.AlbumItem {
        println("otherVersions NewReleaseAlbumPage fromMusicTwoRowItemRenderer: ${renderer.navigationEndpoint?.browseEndpoint}")
        return Innertube.AlbumItem(
            info = Innertube.Info(
                name = renderer.title?.runs?.firstOrNull()?.text ?: "",
                endpoint = renderer.navigationEndpoint?.browseEndpoint
            ),
//            playlistId = renderer.thumbnailOverlay
//                ?.musicItemThumbnailOverlayRenderer?.content
//                ?.musicPlayButtonRenderer?.playNavigationEndpoint
//                ?.watchPlaylistEndpoint?.playlistId ?: return null,
            authors = renderer.subtitle?.runs?.splitBySeparator()?.getOrNull(1)?.oddElements()
                ?.map {
                    Innertube.Info(
                        name = it.text,
                        endpoint = it.navigationEndpoint?.browseEndpoint
                    )
                } ?: emptyList(),
            year = renderer.subtitle?.runs?.lastOrNull()?.text,
            thumbnail = renderer.thumbnailRenderer?.musicThumbnailRenderer?.thumbnail?.thumbnails?.getBestQuality(),
//            explicit = renderer.subtitleBadges?.find {
//                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
//            } != null
        )
    }
}
