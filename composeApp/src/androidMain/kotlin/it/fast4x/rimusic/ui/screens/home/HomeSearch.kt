package it.fast4x.rimusic.ui.screens.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.SearchType
import it.fast4x.rimusic.ui.components.themed.HeaderWithIcon
import it.fast4x.rimusic.ui.items.PlaylistItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.thumbnailShape

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun HomeSearch(
    onSearchType: (SearchType) -> Unit,
    disableScrollingText: Boolean
) {
    val thumbnailSizeDp = 108.dp
    val thumbnailSizePx = thumbnailSizeDp.px
    val lazyGridState = rememberLazyGridState()
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
                    title = stringResource(R.string.search),
                    iconId = R.drawable.search,
                    enabled = false,
                    showIcon = false,
                    modifier = Modifier,
                    onClick = {}
                )

            }

            item(key = "online") {
                PlaylistItem(
                    icon = R.drawable.globe,
                    colorTint = colorPalette().favoritesIcon,
                    name = "${stringResource(R.string.search)} ${stringResource(R.string.online)}",
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onSearchType(SearchType.Online) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "library") {
                PlaylistItem(
                    icon = R.drawable.library,
                    colorTint = colorPalette().favoritesIcon,
                    name = "${stringResource(R.string.search)} ${stringResource(R.string.library)}",
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier
                        .clip(thumbnailShape())
                        .clickable(onClick = { onSearchType(SearchType.Library) })
                        .animateItem(),
                    disableScrollingText = disableScrollingText
                )
            }

            item(key = "gotolink") {
                Modifier
                    .clip(thumbnailShape())
                    .clickable(onClick = { onSearchType(SearchType.Gotolink) })
                PlaylistItem(
                    icon = R.drawable.query_stats,
                    colorTint = colorPalette().favoritesIcon,
                    name = stringResource(R.string.go_to_link),
                    songCount = null,
                    thumbnailSizeDp = thumbnailSizeDp,
                    alternative = true,
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                    disableScrollingText = disableScrollingText
                )
            }

        }

    }
}
