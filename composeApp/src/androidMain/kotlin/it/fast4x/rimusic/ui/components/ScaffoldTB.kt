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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationDefaults.windowInsets
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
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.PlayerPosition
import it.fast4x.rimusic.enums.TransitionEffect
import it.fast4x.rimusic.utils.playerPositionKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.transitionEffectKey
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.ui.components.navigation.header.AppHeader
import it.fast4x.rimusic.ui.components.navigation.nav.HorizontalNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun ScaffoldTB(
    navController: NavController,
    miniPlayer: @Composable (() -> Unit)?,
    topIconButtonId: Int,
    onTopIconButtonClick: () -> Unit,
    showButton1: Boolean,
    topIconButton2Id: Int,
    onTopIconButton2Click: () -> Unit,
    showButton2: Boolean,
    bottomIconButtonId: Int?,
    onBottomIconButtonClick: (() -> Unit)?,
    showBottomButton: Boolean,
    hideTabs: Boolean,
    showTopActions: Boolean,
    onHomeClick: () -> Unit,
    onSettingsClick: (() -> Unit)?,
    onStatisticsClick: (() -> Unit)?,
    onHistoryClick: (() -> Unit)?,
    onSearchClick: (() -> Unit)?,
    tabIndex: Int,
    onTabChanged: (Int) -> Unit,
    tabColumnContent: @Composable (@Composable (Int, String, Int) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(Int) -> Unit
) {
    val horizontalNavBar = HorizontalNavigationBar( tabIndex, onTabChanged, navController, modifier )
    horizontalNavBar.add( tabColumnContent )

    //val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    //var expanded by remember { mutableStateOf(false) }
    val transitionEffect by rememberPreference(transitionEffectKey, TransitionEffect.Scale)
    val playerPosition by rememberPreference(playerPositionKey, PlayerPosition.Bottom)

    androidx.compose.material3.Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
       containerColor = colorPalette().background0,
        topBar = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppHeader( navController ).Draw()

                if ( NavigationBarPosition.Top.isCurrent() )
                    horizontalNavBar.Draw()

                /*
                if (playerEssential != null && playerPosition == PlayerPosition.Top) {
                    val modifierBottomPadding = Modifier
                        .padding(bottom = 5.dp)

                    Row (
                        modifier = modifierBottomPadding
                    ) {
                        playerEssential()
                    }
                }
                 */
            }
        },

        bottomBar = {

        /*

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorPalette.background0)
                ){

         */
                    /*
                    if (playerEssential != null && playerPosition == PlayerPosition.Bottom) {
                        val modifierBottomPadding = if (navigationBarPosition != NavigationBarPosition.Bottom)
                            Modifier.padding( windowInsets
                                .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                                .asPaddingValues()
                                )
                                .padding(bottom = 5.dp)
                        else Modifier
                            .padding(bottom = 5.dp)

                        Row (
                            modifier = modifierBottomPadding
                        ) {
                            playerEssential()
                        }
                    }
                     */

                    if ( NavigationBarPosition.Bottom.isCurrent() )
                        horizontalNavBar.Draw()
                //}
        }

    ) {
        val modifierBoxPadding =
            if ( NavigationBarPosition.Top.isCurrent() )
                Modifier
                    .padding(it)
                    .fillMaxSize()
            else
                Modifier
                    .padding(it)
                    .padding(
                        windowInsets
                            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                            .asPaddingValues()
                    )
                    .fillMaxSize()

        Box(
            modifier = modifierBoxPadding
        ) {

        Row(
            modifier = modifier
                .background(colorPalette().background0)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = tabIndex,
                transitionSpec = {
                    when (transitionEffect) {
                        TransitionEffect.None-> EnterTransition.None togetherWith ExitTransition.None
                        TransitionEffect.Expand -> expandIn(animationSpec = tween(350, easing = LinearOutSlowInEasing), expandFrom = Alignment.TopStart).togetherWith(
                            shrinkOut(animationSpec = tween(350, easing = FastOutSlowInEasing),shrinkTowards = Alignment.TopStart)
                        )
                        TransitionEffect.Fade -> fadeIn(animationSpec = tween(350)).togetherWith(fadeOut(animationSpec = tween(350)))
                        TransitionEffect.Scale -> scaleIn(animationSpec = tween(350)).togetherWith(scaleOut(animationSpec = tween(350)))
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
                content = content,
                label = "",
                modifier = Modifier
                    .fillMaxHeight()
            )
        }
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
