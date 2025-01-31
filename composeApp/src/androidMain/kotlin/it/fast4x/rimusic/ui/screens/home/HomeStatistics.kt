package it.fast4x.rimusic.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.compose.persist.persistList
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.BuiltInPlaylist
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PlaylistSortBy
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.enums.StatisticsType
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.components.themed.InputTextDialog
import it.fast4x.rimusic.ui.items.PlaylistItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.disableScrollingTextKey
import it.fast4x.rimusic.utils.playlistSortByKey
import it.fast4x.rimusic.utils.playlistSortOrderKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.showSearchTabKey
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.thumbnailShape

@SuppressLint("SuspiciousIndentation")
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun HomeStatistics(
    onStatisticsType: (StatisticsType) -> Unit,
    onBuiltInPlaylist: (BuiltInPlaylist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onSearchClick: () -> Unit,
) {
    var isCreatingANewPlaylist by rememberSaveable {
        mutableStateOf(false)
    }

    if (isCreatingANewPlaylist) {
        InputTextDialog(
            onDismiss = { isCreatingANewPlaylist = false },
            title = stringResource(R.string.enter_the_playlist_name),
            value = "",
            placeholder = stringResource(R.string.enter_the_playlist_name),
            setValue = { text ->
                Database.asyncTransaction {
                    insert(Playlist(name = text))
                }
            }
        )
        /*
        TextFieldDialog(
            hintText = stringResource(R.string.enter_the_playlist_name),
            onDismiss = {
                isCreatingANewPlaylist = false
            },
            onDone = { text ->
                query {
                    Database.insert(Playlist(name = text))
                }
            }
        )
         */
    }

    var sortBy by rememberPreference(playlistSortByKey, PlaylistSortBy.DateAdded)
    var sortOrder by rememberPreference(playlistSortOrderKey, SortOrder.Descending)

    val disableScrollingText by rememberPreference(disableScrollingTextKey, false)

    var items by persistList<PlaylistPreview>("home/playlists")

    LaunchedEffect(sortBy, sortOrder) {
        Database.playlistPreviews(sortBy, sortOrder).collect { items = it }
    }
/*
    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (sortOrder == SortOrder.Ascending) 0f else 180f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing)
    )
*/
    val thumbnailSizeDp = 108.dp
    val lazyGridState = rememberLazyGridState()
    val showSearchTab by rememberPreference(showSearchTabKey, false)

    Box(
        modifier = Modifier
            .background(colorPalette().background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(
                if( NavigationBarPosition.Right.isCurrent() )
                    Dimensions.contentWidthRightBar
                else
                    1f
            )
    ) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(Dimensions.thumbnails.song * 2 + Dimensions.itemsVerticalPadding * 2),
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Vertical + WindowInsetsSides.End).asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(Dimensions.itemsVerticalPadding * 2),
            horizontalArrangement = Arrangement.spacedBy(
                space = Dimensions.itemsVerticalPadding * 2,
                alignment = Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(colorPalette().background0)
        ) {
            item(key = "header", contentType = 0, span = { GridItemSpan(maxLineSpan) }) {

                HeaderWithIcon(
                    title = stringResource(R.string.statistics),
                    iconId = R.drawable.search,
                    enabled = true,
                    showIcon = !showSearchTab,
                    modifier = Modifier,
                    onClick = onSearchClick
                )

            }

            item(key = "today") {
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string.today),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onStatisticsType(StatisticsType.Today) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "oneweek") {
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string._1_week),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onStatisticsType(StatisticsType.OneWeek) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "onemonth") {
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string._1_month),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onStatisticsType(StatisticsType.OneMonth) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "threemonths") {
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string._3_month),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onStatisticsType(StatisticsType.ThreeMonths) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "sixmonths") {
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string._6_month),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onStatisticsType(StatisticsType.SixMonths) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "oneyear") {
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string._1_year),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onStatisticsType(StatisticsType.OneYear) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "all") {
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string.all),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onStatisticsType(StatisticsType.All) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

        }
        if( UiType.ViMusic.isCurrent() )
            FloatingActionsContainerWithScrollToTop(
                lazyGridState = lazyGridState,
                iconId = R.drawable.search,
                onClick = onSearchClick
            )
    }
}
