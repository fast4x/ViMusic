package it.fast4x.rimusic.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PlayerPosition
import it.fast4x.rimusic.enums.TransitionEffect
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.utils.playerPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.transitionEffectKey
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.ui.components.navigation.header.AppHeader
import it.fast4x.rimusic.ui.components.navigation.nav.VerticalNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun Scaffold(
    navController: NavController,
    miniPlayer: @Composable (() -> Unit)? = null,
    topIconButtonId: Int = R.drawable.chevron_back,
    onTopIconButtonClick: () -> Unit,
    showButton1: Boolean = false,
    topIconButton2Id: Int = R.drawable.chevron_back,
    onTopIconButton2Click: () -> Unit,
    showButton2: Boolean = false,
    bottomIconButtonId: Int? = R.drawable.search,
    onBottomIconButtonClick: (() -> Unit)? = {},
    showBottomButton: Boolean = false,
    hideTabs: Boolean = false,
    tabIndex: Int = 0,
    onTabChanged: (Int) -> Unit = {},
    showTopActions: Boolean = false,
    tabColumnContent: @Composable (@Composable (Int, String, Int) -> Unit) -> Unit,
    onHomeClick: () -> Unit = { navController.navigate( NavRoutes.home.name ) },
    onSettingsClick: (() -> Unit)? = {},
    onStatisticsClick: (() -> Unit)? = {},
    onHistoryClick: (() -> Unit)? = {},
    onSearchClick: (() -> Unit)? = {},
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val transitionEffect by rememberPreference(transitionEffectKey, TransitionEffect.Scale)
    val playerPosition by rememberPreference(playerPositionKey, PlayerPosition.Bottom)

    if ( NavigationBarPosition.Top.isCurrent() || NavigationBarPosition.Bottom.isCurrent() ) {
            ScaffoldTB(
                navController = navController,
                topIconButtonId = topIconButtonId,
                onTopIconButtonClick = onTopIconButtonClick,
                showButton1 = showButton1,
                topIconButton2Id = topIconButton2Id,
                onTopIconButton2Click = onTopIconButton2Click,
                showButton2 = showButton2,
                tabIndex = tabIndex,
                onTabChanged = onTabChanged,
                tabColumnContent = tabColumnContent,
                showBottomButton = showBottomButton,
                bottomIconButtonId = bottomIconButtonId,
                onBottomIconButtonClick = onBottomIconButtonClick ?: {},
                showTopActions = showTopActions,
                content = content,
                hideTabs = hideTabs,
                onHomeClick = onHomeClick,
                onStatisticsClick = onStatisticsClick,
                onSettingsClick = onSettingsClick,
                onHistoryClick = onHistoryClick,
                onSearchClick = onSearchClick,
                miniPlayer = miniPlayer
            )
    } else {
        //val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val customModifier =
            if( UiType.RiMusic.isCurrent() )
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            else
                Modifier


        androidx.compose.material3.Scaffold(
            modifier = customModifier,
            containerColor = colorPalette().background0,
            topBar = {
                if( UiType.RiMusic.isCurrent() ) AppHeader( navController ).Draw()
            },

            bottomBar = {
                /*
                if (playerEssential != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            //.background(colorPalette.background0)
                            .padding(
                                windowInsets
                                    .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                                    .asPaddingValues()
                            )
                    ) {
                        playerEssential()
                    }

                }

                 */
            }

        ) {

            //**
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {

                Row(
                    modifier = modifier
                        .background( colorPalette().background0 )
                        .fillMaxSize()
                ) {
                    val verticalNavBar = VerticalNavigationBar( tabIndex, onTabChanged, navController )
                    verticalNavBar.add( tabColumnContent )

                    if ( NavigationBarPosition.Left.isCurrent() )
                        verticalNavBar.Draw()

                    val topPadding = if ( UiType.ViMusic.isCurrent() ) 30.dp else 0.dp

                    AnimatedContent(
                        targetState = tabIndex,
                        transitionSpec = {
                            when (transitionEffect) {
                                TransitionEffect.None -> EnterTransition.None togetherWith ExitTransition.None
                                TransitionEffect.Expand -> expandIn(
                                    animationSpec = tween(
                                        350,
                                        easing = LinearOutSlowInEasing
                                    ), expandFrom = Alignment.BottomStart
                                ).togetherWith(
                                    shrinkOut(
                                        animationSpec = tween(
                                            350,
                                            easing = FastOutSlowInEasing
                                        ), shrinkTowards = Alignment.CenterStart
                                    )
                                )

                                TransitionEffect.Fade -> fadeIn(animationSpec = tween(350)).togetherWith(
                                    fadeOut(animationSpec = tween(350))
                                )

                                TransitionEffect.Scale -> scaleIn(animationSpec = tween(350)).togetherWith(
                                    scaleOut(animationSpec = tween(350))
                                )

                                TransitionEffect.SlideHorizontal, TransitionEffect.SlideVertical -> {
                                    val slideDirection = when (targetState > initialState) {
                                        true -> {
                                            if (transitionEffect == TransitionEffect.SlideHorizontal)
                                                AnimatedContentTransitionScope.SlideDirection.Left
                                            else AnimatedContentTransitionScope.SlideDirection.Up
                                        }

                                        false -> {
                                            if (transitionEffect == TransitionEffect.SlideHorizontal)
                                                AnimatedContentTransitionScope.SlideDirection.Right
                                            else AnimatedContentTransitionScope.SlideDirection.Down
                                        }
                                    }

                                    val animationSpec = spring(
                                        dampingRatio = 0.9f,
                                        stiffness = Spring.StiffnessLow,
                                        visibilityThreshold = IntOffset.VisibilityThreshold
                                    )

                                    slideIntoContainer(slideDirection, animationSpec) togetherWith
                                            slideOutOfContainer(slideDirection, animationSpec)
                                }
                            }
                        },
                        content = content, label = "",
                        modifier = Modifier
                            //.fillMaxWidth()
                            .fillMaxHeight()
                            .padding(top = topPadding)
                    )

                    if ( NavigationBarPosition.Right.isCurrent() )
                        verticalNavBar.Draw()

                }
                //**
                Box(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .align(if (playerPosition == PlayerPosition.Top) Alignment.TopCenter
                        else Alignment.BottomCenter)
                ) {
                    miniPlayer?.invoke()
                }
            }
        }
    }

}
