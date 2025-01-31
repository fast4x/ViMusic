package it.fast4x.rimusic.ui.components.navigation.nav

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.NavigationBarPosition
import it.fast4x.rimusic.enums.NavigationBarType
import it.fast4x.rimusic.enums.UiType
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.components.themed.Button
import it.fast4x.rimusic.ui.components.themed.TextIconButton
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.showSearchIconInNav
import it.fast4x.rimusic.showStatsIconInNav

// Shown when "Navigation bar position" is set to "top" or "bottom"
class HorizontalNavigationBar(
    val tabIndex: Int,
    val onTabChanged: (Int) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
): AbstractNavigationBar( navController, modifier ) {

    private fun navButtonProperties(): Modifier {
        val padding: Dp = 4.dp
        val size: Dp = 24.dp
        val border: Shape = CircleShape

        return Modifier.padding( all = padding )
                       .size( size )
                       .clip( shape = border )
    }

    @Composable
    private fun addButton(button: Button, modifier: Modifier = Modifier ) =
        // buttonList() duplicates button instead of updating them.
        // Do NOT use it
        buttonList.add {
            Box( modifier ) { button.Draw() }
        }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun addButton(index: Int, button: Button, modifier: Modifier = Modifier ) =
        // buttonList() duplicates button instead of updating them
        // Do NOT use it
        buttonList.add( index ) {
            Box( modifier ) { button.Draw() }
        }

    @Composable
    private fun bottomPadding(): Dp {
        return if ( NavigationBarPosition.Bottom.isCurrent() )
            with( LocalDensity.current ) {
                WindowInsets.systemBars.getBottom( this ).toDp()
            }
        else
            5.dp
    }

    private fun topPadding(): Dp = 0.dp

    @Composable
    override fun add(buttons: @Composable (@Composable (Int, String, Int) -> Unit) -> Unit) {
        val transition = updateTransition(targetState = tabIndex, label = null)

        buttons { index, text, iconId ->

            val color by transition.animateColor(label = "") {
                if (it == index) colorPalette().text else colorPalette().textDisabled
            }

            val button: Button =
                if ( NavigationBarType.IconOnly.isCurrent() )
                    Button( iconId, color, 12.dp, 20.dp )
                else
                    TextIconButton( text, iconId, color, 0.dp, Dimensions.navigationRailIconOffset * 3 )

            val contentModifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = { onTabChanged(index) })

            addButton( button, contentModifier )
        }
    }

    @Composable
    override fun BackButton(): NavigationButton {
        val button = super.BackButton()
        button.modifier = this.navButtonProperties()
        return button
    }

    @Composable
    override fun SettingsButton(): NavigationButton {
        val button = super.SettingsButton()
        button.modifier = this.navButtonProperties()
        return button
    }

    @Composable
    override fun StatsButton(): NavigationButton {
        val button = super.StatsButton()
        button.modifier = this.navButtonProperties()
        return button
    }

    @Composable
    override fun SearchButton(): NavigationButton {
        val button = super.SearchButton()
        button.modifier = this.navButtonProperties()
        return button
    }

    @Composable
    override fun Draw() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = modifier.padding( top = topPadding(), bottom = bottomPadding() )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.navigationBarHeight - 10.dp)
            ) {

                val scrollState = rememberScrollState()
                val roundedCornerShape =
                    if ( NavigationBarPosition.Bottom.isCurrent() )
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    else
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)

                // Settings button only visible when
                // UI is not RiMusic and current location isn't home screen
                if( UiType.ViMusic.isCurrent() && NavRoutes.home.isNotHere( navController ) )
                    BackButton().Draw()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(roundedCornerShape)
                        .background(colorPalette().background1)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .horizontalScroll(scrollState),
                        content = { buttonList().forEach { it() } }
                    )
                }

                // Search button only visible when
                // UI is not RiMusic and must be explicitly turned on
                if( UiType.ViMusic.isCurrent() && showSearchIconInNav() )
                    SearchButton()

                // Settings button only visible when
                // UI is not RiMusic
                if( UiType.ViMusic.isCurrent() )
                    SettingsButton().Draw()

                // Statistics button only visible when
                // UI is not RiMusic and must be explicitly turned on
                if( UiType.ViMusic.isCurrent() && showStatsIconInNav() )
                    StatsButton()
            }
        }
    }
}