package it.vfsfitvnm.vimusic.ui.screens.album

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.valentinilk.shimmer.shimmer
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.LocalPlayerAwarePaddingValues
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.Album
import it.vfsfitvnm.vimusic.models.DetailedSong
import it.vfsfitvnm.vimusic.models.SongAlbumMap
import it.vfsfitvnm.vimusic.query
import it.vfsfitvnm.vimusic.savers.AlbumResultSaver
import it.vfsfitvnm.vimusic.savers.DetailedSongListSaver
import it.vfsfitvnm.vimusic.ui.components.themed.Header
import it.vfsfitvnm.vimusic.ui.components.themed.HeaderPlaceholder
import it.vfsfitvnm.vimusic.ui.components.themed.NonQueuedMediaItemMenu
import it.vfsfitvnm.vimusic.ui.components.themed.PrimaryButton
import it.vfsfitvnm.vimusic.ui.components.themed.SecondaryTextButton
import it.vfsfitvnm.vimusic.ui.components.themed.TextPlaceholder
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.ui.styling.shimmer
import it.vfsfitvnm.vimusic.ui.views.SongItem
import it.vfsfitvnm.vimusic.utils.asMediaItem
import it.vfsfitvnm.vimusic.utils.center
import it.vfsfitvnm.vimusic.utils.color
import it.vfsfitvnm.vimusic.utils.enqueue
import it.vfsfitvnm.vimusic.utils.forcePlayAtIndex
import it.vfsfitvnm.vimusic.utils.forcePlayFromBeginning
import it.vfsfitvnm.vimusic.utils.medium
import it.vfsfitvnm.vimusic.utils.produceSaveableState
import it.vfsfitvnm.vimusic.utils.secondary
import it.vfsfitvnm.vimusic.utils.semiBold
import it.vfsfitvnm.vimusic.utils.thumbnail
import it.vfsfitvnm.youtubemusic.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun AlbumOverview(
    browseId: String,
) {
    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current
    val context = LocalContext.current

    val albumResult by produceSaveableState(
        initialValue = null,
        stateSaver = AlbumResultSaver,
    ) {
        withContext(Dispatchers.IO) {
            Database.album(browseId).collect { album ->
                if (album?.timestamp == null) {
                    YouTube.album(browseId)?.onSuccess { youtubeAlbum ->
                        Database.upsert(
                            Album(
                                id = browseId,
                                title = youtubeAlbum.title,
                                thumbnailUrl = youtubeAlbum.thumbnail?.url,
                                year = youtubeAlbum.year,
                                authorsText = youtubeAlbum.authors?.joinToString("") { it.name },
                                shareUrl = youtubeAlbum.url,
                                timestamp = System.currentTimeMillis()
                            ),
                            youtubeAlbum.songs
                                ?.map(YouTube.Item.Song::asMediaItem)
                                ?.onEach(Database::insert)
                                ?.mapIndexed { position, mediaItem ->
                                    SongAlbumMap(
                                        songId = mediaItem.mediaId,
                                        albumId = browseId,
                                        position = position
                                    )
                                } ?: emptyList()
                        )
                    }?.onFailure { throwable ->
                        value = Result.failure(throwable)
                    }
                } else {
                    value = Result.success(album)
                }
            }
        }
    }

    val songs by produceSaveableState(
        initialValue = emptyList(),
        stateSaver = DetailedSongListSaver
    ) {
        Database
            .albumSongs(browseId)
            .flowOn(Dispatchers.IO)
            .collect { value = it }
    }

    BoxWithConstraints {
        val thumbnailSizeDp = maxWidth - Dimensions.navigationRailWidth
        val thumbnailSizePx = (thumbnailSizeDp - 32.dp).px

        albumResult?.getOrNull()?.let { album ->
            LazyColumn(
                contentPadding = LocalPlayerAwarePaddingValues.current,
                modifier = Modifier
                    .background(colorPalette.background0)
                    .fillMaxSize()
            ) {
                item(
                    key = "header",
                    contentType = 0
                ) {
                    Column {
                        Header(title = album.title ?: "Unknown") {
                            SecondaryTextButton(
                                text = "Enqueue",
                                isEnabled = songs.isNotEmpty(),
                                onClick = {
                                    binder?.player?.enqueue(songs.map(DetailedSong::asMediaItem))
                                }
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Image(
                                painter = painterResource(
                                    if (album.bookmarkedAt == null) {
                                        R.drawable.bookmark_outline
                                    } else {
                                        R.drawable.bookmark
                                    }
                                ),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorPalette.accent),
                                modifier = Modifier
                                    .clickable {
                                        query {
                                            Database.update(
                                                album.copy(
                                                    bookmarkedAt = if (album.bookmarkedAt == null) {
                                                        System.currentTimeMillis()
                                                    } else {
                                                        null
                                                    }
                                                )
                                            )
                                        }
                                    }
                                    .padding(all = 4.dp)
                                    .size(18.dp)
                            )

                            Image(
                                painter = painterResource(R.drawable.share_social),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorPalette.text),
                                modifier = Modifier
                                    .clickable {
                                        album.shareUrl?.let { url ->
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, url)
                                            }

                                            context.startActivity(
                                                Intent.createChooser(
                                                    sendIntent,
                                                    null
                                                )
                                            )
                                        }
                                    }
                                    .padding(all = 4.dp)
                                    .size(18.dp)
                            )
                        }

                        AsyncImage(
                            model = album.thumbnailUrl?.thumbnail(thumbnailSizePx),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(all = 16.dp)
                                .clip(thumbnailShape)
                                .size(thumbnailSizeDp)
                        )
                    }
                }

                itemsIndexed(
                    items = songs,
                    key = { _, song -> song.id }
                ) { index, song ->
                    SongItem(
                        title = song.title,
                        authors = song.artistsText ?: album.authorsText,
                        durationText = song.durationText,
                        onClick = {
                            binder?.stopRadio()
                            binder?.player?.forcePlayAtIndex(
                                songs.map(DetailedSong::asMediaItem),
                                index
                            )
                        },
                        startContent = {
                            BasicText(
                                text = "${index + 1}",
                                style = typography.s.semiBold.center.color(colorPalette.textDisabled),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .width(Dimensions.thumbnails.song)
                            )
                        },
                        menuContent = {
                            NonQueuedMediaItemMenu(mediaItem = song.asMediaItem)
                        }
                    )
                }
            }

            PrimaryButton(
                iconId = R.drawable.shuffle,
                isEnabled = songs.isNotEmpty(),
                onClick = {
                    binder?.stopRadio()
                    binder?.player?.forcePlayFromBeginning(
                        songs.shuffled().map(DetailedSong::asMediaItem)
                    )
                }
            )
        } ?: albumResult?.exceptionOrNull()?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            ) {
                BasicText(
                    text = "An error has occurred.",
                    style = typography.s.medium.secondary.center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        } ?: Column(
            modifier = Modifier
                .padding(LocalPlayerAwarePaddingValues.current)
                .shimmer()
                .fillMaxSize()
        ) {
            HeaderPlaceholder()

            Spacer(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(all = 16.dp)
                    .clip(thumbnailShape)
                    .size(thumbnailSizeDp)
                    .background(colorPalette.shimmer)
            )

            repeat(3) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .alpha(1f - index * 0.25f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = Dimensions.itemsVerticalPadding)
                        .height(Dimensions.thumbnails.song)
                ) {
                    Spacer(
                        modifier = Modifier
                            .background(color = colorPalette.shimmer, shape = thumbnailShape)
                            .size(Dimensions.thumbnails.song)
                    )

                    Column {
                        TextPlaceholder()
                        TextPlaceholder()
                    }
                }
            }
        }
    }
}
