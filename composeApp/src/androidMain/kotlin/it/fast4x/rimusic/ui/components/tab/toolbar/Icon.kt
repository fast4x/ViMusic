package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.ui.components.navigation.header.TabToolBar

interface Icon: Button {

    @get:DrawableRes
    val iconId: Int
    val color: Color
        @Composable
        get() = colorPalette().text
    val sizeDp: Dp
        get() = TabToolBar.TOOLBAR_ICON_SIZE
    val icon: Painter
        @Composable
        get() = painterResource( this.iconId )
    val modifier: Modifier
        get() = Modifier
    val isEnabled: Boolean
        get() = true

    fun onShortClick()

    @Composable
    override fun ToolBarButton() {
        if( this is Clickable)
            TabToolBar.Icon(
                icon,
                color,
                sizeDp,
                isEnabled,
                modifier,
                this::onShortClick,
                this::onLongClick
            )
        else
            TabToolBar.Icon(
                icon,
                color,
                sizeDp,
                isEnabled,
                modifier,
                this::onShortClick
            )
    }
}