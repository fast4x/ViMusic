package it.fast4x.rimusic.ui.components.themed

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.PlaylistSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.transaction
import it.fast4x.rimusic.ui.items.PlaylistItem
import it.fast4x.rimusic.PINNED_PREFIX
import it.fast4x.rimusic.PIPED_PREFIX
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.MONTHLY_PREFIX
import it.fast4x.rimusic.utils.menuStyleKey
import it.fast4x.rimusic.utils.playlistSortByKey
import it.fast4x.rimusic.utils.playlistSortOrderKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import kotlinx.coroutines.Dispatchers
import me.knighthat.colorPalette
import me.knighthat.typography

@ExperimentalTextApi
@SuppressLint("SuspiciousIndentation")
@UnstableApi
@ExperimentalAnimationApi
@Composable
fun PlaylistsItemGridMenu(
    navController: NavController,
    onDismiss: () -> Unit,
    onSelectUnselect: (() -> Unit)? = null,
    onSelect: (() -> Unit)? = null,
    onUncheck: (() -> Unit)? = null,
    playlist: PlaylistPreview? = null,
    modifier: Modifier = Modifier,
    onPlayNext: (() -> Unit)? = null,
    onEnqueue: (() -> Unit)? = null,
    onImportOnlinePlaylist: (() -> Unit)? = null,
    onAddToPlaylist: ((PlaylistPreview) -> Unit)? = null,
    showOnSyncronize: Boolean = false,
    onSyncronize: (() -> Unit)? = null,
    onRenumberPositions: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRename: (() -> Unit)? = null,
    showonListenToYT: Boolean = false,
    onListenToYT: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null,
    onImport: (() -> Unit)? = null,
    onGoToPlaylist: ((Long) -> Unit)? = null
    ) {
    var isViewingPlaylists by remember {
        mutableStateOf(false)
    }

    var height by remember {
        mutableStateOf(0.dp)
    }

    val binder = LocalPlayerServiceBinder.current
    val menuStyle by rememberPreference(
        menuStyleKey,
        MenuStyle.List
    )
    val thumbnailSizeDp = Dimensions.thumbnails.song + 20.dp
    val thumbnailSizePx = thumbnailSizeDp.px
    val thumbnailArtistSizeDp = Dimensions.thumbnails.song + 10.dp
    val thumbnailArtistSizePx = thumbnailArtistSizeDp.px


    AnimatedContent(
        targetState = isViewingPlaylists,
        transitionSpec = {
            val animationSpec = tween<IntOffset>(400)
            val slideDirection = if (targetState) AnimatedContentTransitionScope.SlideDirection.Left
            else AnimatedContentTransitionScope.SlideDirection.Right

            slideIntoContainer(slideDirection, animationSpec) togetherWith
                    slideOutOfContainer(slideDirection, animationSpec)
        }, label = ""
    ) { currentIsViewingPlaylists ->
        if (currentIsViewingPlaylists) {
            val sortBy by rememberPreference(playlistSortByKey, PlaylistSortBy.DateAdded)
            val sortOrder by rememberPreference(playlistSortOrderKey, SortOrder.Descending)
            val playlistPreviews by remember {
                Database.playlistPreviews(sortBy, sortOrder)
            }.collectAsState(initial = emptyList(), context = Dispatchers.IO)

            val pinnedPlaylists = playlistPreviews.filter {
                it.playlist.name.startsWith(PINNED_PREFIX, 0, true)
            }

            val unpinnedPlaylists = playlistPreviews.filter {
                !it.playlist.name.startsWith(PINNED_PREFIX, 0, true) &&
                !it.playlist.name.startsWith(MONTHLY_PREFIX, 0, true) //&&
                //!it.playlist.name.startsWith(PIPED_PREFIX, 0, true)
            }

            var isCreatingNewPlaylist by rememberSaveable {
                mutableStateOf(false)
            }

            if (isCreatingNewPlaylist && onAddToPlaylist != null) {
                InputTextDialog(
                    onDismiss = { isCreatingNewPlaylist = false },
                    title = stringResource(R.string.enter_the_playlist_name),
                    value = "",
                    placeholder = stringResource(R.string.enter_the_playlist_name),
                    setValue = { text ->
                        onDismiss()
                        transaction {
                            val playlistId = Database.insert(Playlist(name = text))
                            onAddToPlaylist(
                                PlaylistPreview(
                                    Playlist(
                                        id = playlistId,
                                        name = text
                                    ), 0
                                )
                            )
                        }
                    }
                )
            }

            BackHandler {
                isViewingPlaylists = false
            }

            Menu(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { isViewingPlaylists = false },
                        icon = R.drawable.chevron_back,
                        color = colorPalette().textSecondary,
                        modifier = Modifier
                            .padding(all = 4.dp)
                            .size(20.dp)
                    )

                    if (onAddToPlaylist != null) {
                        SecondaryTextButton(
                            text = stringResource(R.string.new_playlist),
                            onClick = { isCreatingNewPlaylist = true },
                            alternative = true
                        )
                    }
                }

                if (pinnedPlaylists.isNotEmpty()) {
                    BasicText(
                        text = stringResource(R.string.pinned_playlists),
                        style = typography().m.semiBold,
                        modifier = modifier.padding(start = 20.dp, top = 5.dp)
                    )

                    onAddToPlaylist?.let { onAddToPlaylist ->
                        pinnedPlaylists.forEach { playlistPreview ->
                            MenuEntry(
                                icon = R.drawable.add_in_playlist,
                                text = playlistPreview.playlist.name.substringAfter(
                                    PINNED_PREFIX
                                ),
                                secondaryText = "${playlistPreview.songCount} " + stringResource(
                                    R.string.songs
                                ),
                                onClick = {
                                    onDismiss()
                                    onAddToPlaylist(
                                        PlaylistPreview(
                                            playlistPreview.playlist,
                                            playlistPreview.songCount
                                        )
                                    )
                                },
                                trailingContent = {
                                    IconButton(
                                        icon = R.drawable.open,
                                        color = colorPalette().text,
                                        onClick = {
                                            if (onGoToPlaylist != null) {
                                                onGoToPlaylist(playlistPreview.playlist.id)
                                                onDismiss()
                                            }
                                            navController.navigate(route = "${NavRoutes.localPlaylist.name}/${playlistPreview.playlist.id}")
                                        },
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                if (unpinnedPlaylists.isNotEmpty()) {
                    BasicText(
                        text = stringResource(R.string.playlists),
                        style = typography().m.semiBold,
                        modifier = modifier.padding(start = 20.dp, top = 5.dp)
                    )

                    onAddToPlaylist?.let { onAddToPlaylist ->
                        unpinnedPlaylists.forEach { playlistPreview ->
                            MenuEntry(
                                icon = R.drawable.add_in_playlist,
                                text = playlistPreview.playlist.name,
                                secondaryText = "${playlistPreview.songCount} " + stringResource(
                                    R.string.songs
                                ),
                                onClick = {
                                    onDismiss()
                                    onAddToPlaylist(
                                        PlaylistPreview(
                                            playlistPreview.playlist,
                                            playlistPreview.songCount
                                        )
                                    )
                                },
                                trailingContent = {
                                    if (playlistPreview.playlist.name.startsWith(PIPED_PREFIX, 0, true))
                                        Image(
                                            painter = painterResource(R.drawable.piped_logo),
                                            contentDescription = null,
                                            colorFilter = ColorFilter.tint(colorPalette().red),
                                            modifier = Modifier
                                                .size(18.dp)
                                        )

                                    IconButton(
                                        icon = R.drawable.open,
                                        color = colorPalette().text,
                                        onClick = {
                                            if (onGoToPlaylist != null) {
                                                onGoToPlaylist(playlistPreview.playlist.id)
                                                onDismiss()
                                            }
                                            navController.navigate(route = "${NavRoutes.localPlaylist.name}/${playlistPreview.playlist.id}")
                                        },
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        } else {
            val selectText = "${stringResource(R.string.item_select)}/${stringResource(R.string.item_deselect)}"
            val colorPalette = colorPalette()

            GridMenu(
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp + WindowInsets.systemBars.asPaddingValues()
                        .calculateBottomPadding()
                ),
                topContent = {
                    if (playlist != null) {
                        PlaylistItem(
                            playlist = playlist,
                            thumbnailSizePx = thumbnailSizePx,
                            thumbnailSizeDp = thumbnailSizeDp,
                            modifier = Modifier.height(90.dp)
                        )
                    }
                }
            ) {

                onSelectUnselect?.let { onSelectUnselect ->
                    GridMenuItem(
                        icon = R.drawable.checked,
                        title = R.string.item_select,
                        titleString = selectText,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onSelectUnselect()
                        }
                    )
                }

                onPlayNext?.let { onPlayNext ->
                    GridMenuItem(
                        icon = R.drawable.play_skip_forward,
                        title = R.string.play_next,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onPlayNext()
                        }
                    )
                }

                onEnqueue?.let { onEnqueue ->
                    GridMenuItem(
                        icon = R.drawable.enqueue,
                        title = R.string.enqueue,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onEnqueue()
                        }
                    )
                }

                if (showOnSyncronize) onSyncronize?.let { onSyncronize ->
                    GridMenuItem(
                        icon = R.drawable.sync,
                        title = R.string.sync,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onSyncronize()
                        }
                    )
                }

                onImportOnlinePlaylist?.let { onImportOnlinePlaylist ->
                    GridMenuItem(
                        icon = R.drawable.add_in_playlist,
                        title = R.string.import_playlist,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onImportOnlinePlaylist()
                        }
                    )
                }

                onAddToPlaylist?.let { onAddToPlaylist ->
                    GridMenuItem(
                        icon = R.drawable.add_in_playlist,
                        title = R.string.add_to_playlist,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            isViewingPlaylists = true
                        }
                    )
                }

                onRename?.let { onRename ->
                    GridMenuItem(
                        icon = R.drawable.title_edit,
                        title = R.string.rename,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onRename()
                        }
                    )
                }

                onDelete?.let { onDelete ->
                    GridMenuItem(
                        icon = R.drawable.trash,
                        title = R.string.delete,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onDelete()
                        }
                    )
                }

                onRenumberPositions?.let { onRenumberPositions ->
                    GridMenuItem(
                        icon = R.drawable.position,
                        title = R.string.renumber_songs_positions,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onRenumberPositions()
                        }
                    )
                }

                if (showonListenToYT) onListenToYT?.let { onListenToYT ->
                    GridMenuItem(
                        icon = R.drawable.play,
                        title = R.string.listen_on_youtube,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onListenToYT()
                        }
                    )
                }

                onExport?.let { onExport ->
                    GridMenuItem(
                        icon = R.drawable.export,
                        title = R.string.export_playlist,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onExport()
                        }
                    )
                }

                onImport?.let { onImport ->
                    GridMenuItem(
                        icon = R.drawable.resource_import,
                        title = R.string.import_playlist,
                        colorIcon = colorPalette.text,
                        colorText = colorPalette.text,
                        onClick = {
                            onDismiss()
                            onImport()
                        }
                    )
                }

            }

        }
    }

}