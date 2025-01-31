package it.fast4x.innertube.requests

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.models.BrowseResponse
import it.fast4x.innertube.models.MusicCarouselShelfRenderer
import it.fast4x.innertube.models.MusicShelfRenderer
import it.fast4x.innertube.models.SectionListRenderer
import it.fast4x.innertube.models.bodies.BrowseBody
import it.fast4x.innertube.utils.findSectionByTitle
import it.fast4x.innertube.utils.from
import it.fast4x.innertube.utils.runCatchingNonCancellable

suspend fun Innertube.artistPage(body: BrowseBody): Result<Innertube.ArtistInfoPage>? =
    runCatchingNonCancellable {
        val response = client.post(browse) {
            setBody(body)
            mask("contents,header")
        }.body<BrowseResponse>()

        fun findSectionByTitle(text: String): SectionListRenderer.Content? {
            return response
                .contents
                ?.singleColumnBrowseResultsRenderer
                ?.tabs
                ?.get(0)
                ?.tabRenderer
                ?.content
                ?.sectionListRenderer
                ?.findSectionByTitle(text)
        }

        val songsSection = findSectionByTitle("Songs")?.musicShelfRenderer
        val albumsSection = findSectionByTitle("Albums")?.musicCarouselShelfRenderer
        val singlesSection = findSectionByTitle("Singles")?.musicCarouselShelfRenderer
        val playlistsSection = findSectionByTitle("Playlists")?.musicCarouselShelfRenderer

        Innertube.ArtistInfoPage(
            name = response
                .header
                ?.musicImmersiveHeaderRenderer
                ?.title
                ?.text,
            description = response
                .header
                ?.musicImmersiveHeaderRenderer
                ?.description
                ?.text,
            subscriberCountText = response
                .header
                ?.musicImmersiveHeaderRenderer
                ?.subscriptionButton
                ?.subscribeButtonRenderer
                ?.subscriberCountText
                ?.text,
            thumbnail = (response
                .header
                ?.musicImmersiveHeaderRenderer
                ?.foregroundThumbnail
                ?: response
                    .header
                    ?.musicImmersiveHeaderRenderer
                    ?.thumbnail)
                ?.musicThumbnailRenderer
                ?.thumbnail
                ?.thumbnails
                ?.getBestQuality(),
                //?.getOrNull(0),
            shuffleEndpoint = response
                .header
                ?.musicImmersiveHeaderRenderer
                ?.playButton
                ?.buttonRenderer
                ?.navigationEndpoint
                ?.watchEndpoint,
            radioEndpoint = response
                .header
                ?.musicImmersiveHeaderRenderer
                ?.startRadioButton
                ?.buttonRenderer
                ?.navigationEndpoint
                ?.watchEndpoint,
            songs = songsSection
                ?.contents
                ?.mapNotNull(MusicShelfRenderer.Content::musicResponsiveListItemRenderer)
                ?.mapNotNull(Innertube.SongItem::from),
            songsEndpoint = songsSection
                ?.bottomEndpoint
                ?.browseEndpoint,
            albums = albumsSection
                ?.contents
                ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
                ?.mapNotNull(Innertube.AlbumItem::from),
            albumsEndpoint = albumsSection
                ?.header
                ?.musicCarouselShelfBasicHeaderRenderer
                ?.moreContentButton
                ?.buttonRenderer
                ?.navigationEndpoint
                ?.browseEndpoint,
            singles = singlesSection
                ?.contents
                ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
                ?.mapNotNull(Innertube.AlbumItem::from),
            singlesEndpoint = singlesSection
                ?.header
                ?.musicCarouselShelfBasicHeaderRenderer
                ?.moreContentButton
                ?.buttonRenderer
                ?.navigationEndpoint
                ?.browseEndpoint,
            playlists = playlistsSection
                ?.contents
                ?.mapNotNull(MusicCarouselShelfRenderer.Content::musicTwoRowItemRenderer)
                ?.mapNotNull(Innertube.PlaylistItem::from)
        )
    }
