package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.Menu
import it.fast4x.rimusic.utils.menuStyleKey
import it.fast4x.rimusic.utils.rememberPreference

class EllipsisMenuComponent private constructor(
    private val buttons: () -> List<Button>,
    override val menuState: MenuState,
    override val styleState: MutableState<MenuStyle>
) : Menu {

    companion object {
        @JvmStatic
        @Composable
        fun init( items: () -> List<Button> ) = EllipsisMenuComponent(
            items,
            LocalMenuState.current,
            rememberPreference( menuStyleKey, MenuStyle.List )
        )
    }

    var style: MenuStyle = styleState.value
        set(value) {
            styleState.value = value
            field = value
        }
    override val iconId: Int = R.drawable.ellipsis_horizontal

    @Composable
    override fun ListMenu() {
        Menu(
            Modifier.fillMaxHeight(0.4f)
                .onPlaced { it.size.height.dp * 0.5f }
        ) {
            buttons().forEach {
                if( it is MenuIcon)
                    it.ListMenuItem()
            }
        }
    }

    @Composable
    override fun GridMenu() {
        it.fast4x.rimusic.ui.components.themed.GridMenu(
            contentPadding = PaddingValues(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp + WindowInsets.systemBars.asPaddingValues()
                    .calculateBottomPadding()
            )
        ) {
            items( buttons(), Button::hashCode ) {
                if( it is MenuIcon)
                    it.GridMenuItem()
            }
        }
    }

    @Composable
    override fun MenuComponent() {
        if( style == MenuStyle.Grid )
            GridMenu()
        else
            ListMenu()
    }
}