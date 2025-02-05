package it.fast4x.rimusic.utils

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import it.fast4x.innertube.Innertube
import it.fast4x.innertube.YtMusic
import it.fast4x.innertube.utils.completed
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.Database.Companion.albumsByTitleAsc
import it.fast4x.rimusic.Database.Companion.getAlbumsList
import it.fast4x.rimusic.Database.Companion.getArtistsList
import it.fast4x.rimusic.Database.Companion.preferitesArtistsByName
import it.fast4x.rimusic.Database.Companion.update
import it.fast4x.rimusic.R
import it.fast4x.rimusic.YTEDITABLEPLAYLIST_PREFIX
import it.fast4x.rimusic.YTP_PREFIX
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.cleanPrefix
import it.fast4x.rimusic.enums.PlaylistSongSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.isAutoSyncEnabled
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Artist
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.SongPlaylistMap
import it.fast4x.rimusic.ui.components.tab.toolbar.Descriptive
import it.fast4x.rimusic.ui.components.tab.toolbar.DynamicColor
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.ui.components.themed.SmartMessage
import it.fast4x.rimusic.ui.screens.settings.isYouTubeSyncEnabled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

suspend fun importYTMPrivatePlaylists(): Boolean {
    if (isYouTubeSyncEnabled()) {

        SmartMessage(
            message = appContext().resources.getString(R.string.syncing),
            durationLong = true,
            context = appContext(),
        )

        Innertube.library("FEmusic_liked_playlists").completed().onSuccess { page ->

            val ytmPrivatePlaylists = page.items.filterIsInstance<Innertube.PlaylistItem>()
                .filterNot { it.key == "VLLM" || it.key == "VLSE" }

            val localPlaylists = Database.ytmPrivatePlaylists().firstOrNull()

            (localPlaylists?.filter { playlist -> playlist.browseId !in ytmPrivatePlaylists.map { if (it.key.startsWith("VL")) it.key.substringAfter("VL") else it.key }  })?.forEach { playlist ->
                Database.asyncTransaction{ delete(playlist) }
            }

            ytmPrivatePlaylists.forEach { remotePlaylist ->
                withContext(Dispatchers.IO) {
                    val playlistIdChecked =
                        if (remotePlaylist.key.startsWith("VL")) remotePlaylist.key.substringAfter("VL") else remotePlaylist.key
                    println("Playlist ${localPlaylists?.map { it?.browseId }}")
                    var localPlaylist =
                        localPlaylists?.find { it?.browseId == playlistIdChecked }

                    println("Local playlist: $localPlaylist")
                    println("Remote playlist: $remotePlaylist")
                    if (localPlaylist == null && playlistIdChecked.isNotEmpty()) {
                        localPlaylist = Playlist(
                            name = remotePlaylist.title ?: "",
                            browseId = playlistIdChecked,
                            isYoutubePlaylist = true,
                            isEditable = (remotePlaylist.isEditable == true)
                        )
                        Database.insert(localPlaylist.copy(browseId = playlistIdChecked))
                    }

                    Database.playlistWithSongsByBrowseId(playlistIdChecked).firstOrNull()?.let {
                          if (it.playlist.id != 0L)
                            it.playlist.id.let { id ->
                                ytmPrivatePlaylistSync(
                                    it.playlist,
                                    id
                                )
                            }
                    }
                }
            }

        }.onFailure {
            println("Error importing YTM private playlists: ${it.stackTraceToString()}")
            return false
        }
        return true
    } else
        return false
}

@OptIn(UnstableApi::class)
fun ytmPrivatePlaylistSync(playlist: Playlist, playlistId: Long) {
    playlist.let { plist ->
        Database.asyncTransaction {
            runBlocking(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    plist.browseId?.let {
                        YtMusic.getPlaylist(
                            playlistId = it
                        ).completed()
                    }
                }
            }?.getOrNull()?.let { remotePlaylist ->

                println("ytmPrivatePlaylistSync Remote playlist editable: ${remotePlaylist.isEditable}")
                val playlistIdChecked =
                    if (remotePlaylist.playlist.key.startsWith("VL")) remotePlaylist.playlist.key.substringAfter("VL") else remotePlaylist.playlist.key

                if (remotePlaylist.isEditable == true)
                    Database.update(playlist.copy(browseId = playlistIdChecked))

                if (remotePlaylist.songs.isNotEmpty()) {
                    //Database.clearPlaylist(playlistId)

                    remotePlaylist.songs
                        .map(Innertube.SongItem::asMediaItem)
                        .onEach(Database::insert)
                        .mapIndexed { position, mediaItem ->
                            SongPlaylistMap(
                                songId = mediaItem.mediaId,
                                playlistId = playlistId,
                                position = position
                            )
                        }.let(Database::insertSongPlaylistMaps)
                }
                runBlocking(Dispatchers.IO) {
                    val localPlaylistSongs = Database.songsPlaylist(playlistId,PlaylistSongSortBy.Position, SortOrder.Ascending).firstOrNull()

                    localPlaylistSongs?.filter {it.asMediaItem.mediaId !in remotePlaylist.songs.map { it.asMediaItem.mediaId }}?.forEach { song ->
                        deleteSongFromPlaylist(song.asMediaItem.mediaId,playlistId)
                    }
                }
            }
        }

    }
}

suspend fun importYTMSubscribedChannels(): Boolean {
    println("importYTMSubscribedChannels isYouTubeSyncEnabled() = ${isYouTubeSyncEnabled()} and isAutoSyncEnabled() = ${isAutoSyncEnabled()}")
    if (isYouTubeSyncEnabled()) {

        SmartMessage(
            message = appContext().resources.getString(R.string.syncing),
            durationLong = true,
            context = appContext(),
        )

        Innertube.library("FEmusic_library_corpus_artists").completed().onSuccess { page ->

            val ytmArtists = page.items.filterIsInstance<Innertube.ArtistItem>()

            println("YTM artists: $ytmArtists")

            ytmArtists.forEach { remoteArtist ->
                withContext(Dispatchers.IO) {

                    var localArtist = Database.artist(remoteArtist.key).firstOrNull()
                    println("Local artist: $localArtist")
                    println("Remote artist: $remoteArtist")

                    if (localArtist == null) {
                        localArtist = Artist(
                            id = remoteArtist.key,
                            name = (YTP_PREFIX + remoteArtist.title),
                            thumbnailUrl = remoteArtist.thumbnail?.url,
                            bookmarkedAt = System.currentTimeMillis()
                        )
                        Database.insert(localArtist)
                    } else {
                        localArtist.copy(
                            name = (YTP_PREFIX + remoteArtist.title),
                            bookmarkedAt = localArtist.bookmarkedAt ?: System.currentTimeMillis(),
                            thumbnailUrl = remoteArtist.thumbnail?.url
                        ).let(::update)
                    }


                }
            }
            val Artists = getArtistsList().firstOrNull()
            Database.asyncTransaction {
                Artists?.filter {artist -> artist?.name?.startsWith(YTP_PREFIX) == true && artist.id !in ytmArtists.map { it.key } }?.forEach { artist ->
                    if (artist != null) delete(artist)
                }
            }
        }
            .onFailure {
                println("Error importing YTM subscribed artists channels: ${it.stackTraceToString()}")
                return false
            }
        return true
    } else
        return false
}

suspend fun importYTMLikedAlbums(): Boolean {
    println("importYTMLikedAlbums isYouTubeSyncEnabled() = ${isYouTubeSyncEnabled()} and isAutoSyncEnabled() = ${isAutoSyncEnabled()}")
    if (isYouTubeSyncEnabled()) {

        SmartMessage(
            message = appContext().resources.getString(R.string.syncing),
            durationLong = true,
            context = appContext(),
        )

        Innertube.library("FEmusic_liked_albums").completed().onSuccess { page ->

            val ytmAlbums = page.items.filterIsInstance<Innertube.AlbumItem>()

            println("YTM albums: $ytmAlbums")

            ytmAlbums.forEach { remoteAlbum ->
                withContext(Dispatchers.IO) {

                    var localAlbum = Database.album(remoteAlbum.key).firstOrNull()
                    println("Local album: $localAlbum")
                    println("Remote album: $remoteAlbum")

                    if (localAlbum == null) {
                        localAlbum = Album(
                            id = remoteAlbum.key,
                            title = (YTP_PREFIX + remoteAlbum.title) ?: "",
                            thumbnailUrl = remoteAlbum.thumbnail?.url,
                            bookmarkedAt = System.currentTimeMillis(),
                            year = remoteAlbum.year,
                            authorsText = remoteAlbum.authors?.getOrNull(1)?.name
                        )
                        Database.insert(localAlbum)
                    } else {
                        localAlbum.copy(
                            title = YTP_PREFIX + localAlbum.title,
                            bookmarkedAt = localAlbum.bookmarkedAt ?: System.currentTimeMillis(),
                            thumbnailUrl = remoteAlbum.thumbnail?.url)
                            .let(::update)
                    }

                }
            }
            val Albums = getAlbumsList().firstOrNull()
            Database.asyncTransaction {
                Albums?.filter {album -> album?.title?.startsWith(YTP_PREFIX) == true && album.id !in ytmAlbums.map { it.key } }?.forEach { album->
                    if (album != null) delete(album)
                }
            }
        }
            .onFailure {
                println("Error importing YTM liked albums: ${it.stackTraceToString()}")
                return false
            }
        return true
    } else
        return false
}

@Composable
fun autoSyncToolbutton(messageId: Int): MenuIcon = object : MenuIcon, DynamicColor, Descriptive {

    override var isFirstColor: Boolean by rememberPreference(autosyncKey, false)
    override val iconId: Int = R.drawable.sync
    override val messageId: Int = messageId
    override val menuIconTitle: String
        @Composable
        get() = stringResource(messageId)

    override fun onShortClick() {
        isFirstColor = !isFirstColor
    }
}

