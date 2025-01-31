package it.fast4x.rimusic.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.colorPalette

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullToRefreshBox(
    modifier: Modifier = Modifier,
    refreshing: Boolean,
    onRefresh: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val refreshState = rememberPullRefreshState(refreshing, onRefresh)
    Box(
        modifier = modifier.pullRefresh(refreshState),
    ) {
        content()
        PullRefreshIndicator(
            refreshing, refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
            backgroundColor = colorPalette().favoritesIcon

        )
    }
}