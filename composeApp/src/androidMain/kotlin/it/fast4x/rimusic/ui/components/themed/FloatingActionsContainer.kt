package it.fast4x.rimusic.ui.components.themed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import it.fast4x.rimusic.LocalPlayerAwareWindowInsets
import it.fast4x.rimusic.LocalPlayerSheetState
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.ui.components.FabItem
import it.fast4x.rimusic.ui.components.MultiFloatingActionsButton
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.utils.ScrollingInfo
import it.fast4x.rimusic.utils.floatActionIconOffsetXkey
import it.fast4x.rimusic.utils.floatActionIconOffsetYkey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.scrollingInfo
import it.fast4x.rimusic.utils.smoothScrollToTop
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@UnstableApi
@ExperimentalAnimationApi
@Composable
fun BoxScope.MultiFloatingActionsContainer(
    modifier: Modifier = Modifier,
    useAsActionsMenu: Boolean = false,
    iconId: Int,
    onClick: () -> Unit,
    onClickSettings: (() -> Unit)? = null,
    onClickSearch: (() -> Unit)? = null
) {
    val additionalBottomPadding =
        if ( NavigationBarPosition.Bottom.isCurrent() )
            Dimensions.additionalVerticalSpaceForFloatingAction
        else
            0.dp
    //val bottomPaddingValues = windowInsets.only(WindowInsetsSides.Bottom).asPaddingValues()
    val density = LocalDensity.current
    val windowsInsets = WindowInsets.systemBars
    val bottomDp = with(density) { windowsInsets.getBottom(density).toDp() }

    val playerSheetState = LocalPlayerSheetState.current
    val bottomPadding = if (!playerSheetState.isVisible) bottomDp + Dimensions.collapsedPlayer + additionalBottomPadding else bottomDp + additionalBottomPadding

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp)
            .padding(bottom = bottomPadding)
    ) {

        MultiFloatingActionsButton(
            useAsActionsMenu = useAsActionsMenu,
            fabIcon = painterResource(iconId),
            items =
                arrayListOf(
                    FabItem(
                        icon = painterResource(R.drawable.settings),
                        label = "Settings",
                        onFabItemClicked = {
                            if (onClickSettings != null) {
                                onClickSettings()
                            }
                        }
                    ),
                    FabItem(
                        icon = painterResource(R.drawable.search),
                        label = stringResource(R.string.search),
                        onFabItemClicked = {
                            if (onClickSearch != null) {
                                onClickSearch()
                            }
                        }
                    )
                )
            ,
            onClick = { onClick() }
        )
    }

}

@ExperimentalAnimationApi
@Composable
fun BoxScope.FloatingActionsContainerWithScrollToTop(
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    iconId: Int? = null,
    onClick: (() -> Unit)? = null,
    windowInsets: WindowInsets = LocalPlayerAwareWindowInsets.current
) {
    val transitionState = remember {
        MutableTransitionState<ScrollingInfo?>(ScrollingInfo())
    }.apply { targetState = if (visible) lazyGridState.scrollingInfo() else null }

    FloatingActions(
        transitionState = transitionState,
        onScrollToTop = lazyGridState::smoothScrollToTop,
        iconId = iconId,
        onClick = onClick,
        windowInsets = windowInsets,
        modifier = modifier
    )
}

@ExperimentalAnimationApi
@Composable
fun BoxScope.FloatingActionsContainerWithScrollToTop(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    iconId: Int? = null,
    onClick: (() -> Unit)? = null,
    windowInsets: WindowInsets = LocalPlayerAwareWindowInsets.current
) {
    val transitionState = remember {
        MutableTransitionState<ScrollingInfo?>(ScrollingInfo())
    }.apply { targetState = if (visible) lazyListState.scrollingInfo() else null }

    FloatingActions(
        transitionState = transitionState,
        onScrollToTop = lazyListState::smoothScrollToTop,
        iconId = iconId,
        onClick = onClick,
        windowInsets = windowInsets,
        modifier = modifier
    )
}

@ExperimentalAnimationApi
@Composable
fun BoxScope.FloatingActionsContainerWithScrollToTop(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    iconId: Int? = null,
    onClick: (() -> Unit)? = null,
    windowInsets: WindowInsets = LocalPlayerAwareWindowInsets.current
) {
    val transitionState = remember {
        MutableTransitionState<ScrollingInfo?>(ScrollingInfo())
    }.apply { targetState = if (visible) scrollState.scrollingInfo() else null }


    FloatingActions(
        transitionState = transitionState,
        iconId = iconId,
        onClick = onClick,
        windowInsets = windowInsets,
        modifier = modifier
    )
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun BoxScope.FloatingActions(
    transitionState: MutableTransitionState<ScrollingInfo?>,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    onScrollToTop: (suspend () -> Unit)? = null,
    iconId: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val transition = rememberTransition(transitionState, "")
    val additionalBottomPadding = if ( NavigationBarPosition.Bottom.isCurrent() )
        Dimensions.additionalVerticalSpaceForFloatingAction else 0.dp
    //val bottomPaddingValues = windowInsets.only(WindowInsetsSides.Bottom).asPaddingValues()
    val density = LocalDensity.current
    val windowsInsets = WindowInsets.systemBars
    val bottomDp = with(density) { windowsInsets.getBottom(density).toDp() }

    val playerSheetState = LocalPlayerSheetState.current
    val bottomPadding = if (!playerSheetState.isVisible) bottomDp + Dimensions.collapsedPlayer else bottomDp

    var offsetX = rememberPreference(floatActionIconOffsetXkey, 0F )
    var offsetY = rememberPreference(floatActionIconOffsetYkey, 0F )

    val modifierActions = Modifier
        .padding(bottom = 16.dp)
        .padding(bottom = bottomPadding)
        .offset {
            IntOffset(offsetX.value.toInt(), offsetY.value.toInt())
        }
        .pointerInput(Unit) {
            /*
            detectDragGestures { change, dragAmount ->
                change.consume()
                offsetX += dragAmount.x
                offsetY += dragAmount.y
            }
             */
            detectDragGesturesAfterLongPress { change, dragAmount ->
                change.consume()
                offsetX.value += dragAmount.x
                offsetY.value += dragAmount.y

            }
        }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .align(Alignment.BottomEnd)
            //.padding(end = 16.dp)
            //.padding(bottom = bottomPadding)
            /*
            .padding(
                windowInsets
                    .only(WindowInsetsSides.End)
                    .asPaddingValues()
            )
             */
    ) {
        onScrollToTop?.let {
            transition.AnimatedVisibility(
                visible = { it?.isScrollingDown == false && it.isFar }, //{ it?.isScrollingDown == true},
                enter = slideInVertically(tween(500, if (iconId == null) 0 else 100)) { it },
                exit = slideOutVertically(tween(500, 0)) { it },
            ) {
                val coroutineScope = rememberCoroutineScope()
                PrimaryButton(
                    iconId = R.drawable.chevron_up,
                    onClick = {
                        coroutineScope.launch {
                            onScrollToTop()
                        }
                    },
                    enabled = transition.targetState?.isScrollingDown == false && transition.targetState?.isFar == true, //transition.targetState?.isScrollingDown == false,
                    modifier = modifierActions
                )
                /*
                SecondaryCircleButton(
                    onClick = {
                        coroutineScope.launch {
                            onScrollToTop()
                        }
                    },
                    enabled = transition.targetState?.isScrollingDown == true, //transition.targetState?.isScrollingDown == false && transition.targetState?.isFar == true,
                    iconId = R.drawable.chevron_up,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .padding(bottomPaddingValues)
                )
                 */
            }
        }

        iconId?.let {
            onClick?.let {
                transition.AnimatedVisibility(
                    visible = {
                              true
                        //it?.isScrollingDown == false
                              },
                    enter = slideInVertically(tween(500, 0)) { it },
                    exit = slideOutVertically(tween(500, 100)) { it },
                ) {
                    PrimaryButton(
                        iconId = iconId,
                        onClick = onClick,
                        enabled = true, //transition.targetState?.isScrollingDown == false,
                        modifier = modifierActions
                    )
                }
            }
        }
    }
}

