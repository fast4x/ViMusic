package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.colorPalette

class DropdownMenu(
    val expanded: Boolean,
    val containerColor: Color = Color.Transparent,
    val modifier: Modifier = Modifier,
    val onDismissRequest: () -> Unit
) {

    private val _components: MutableList<@Composable () -> Unit> = mutableListOf()

    @Composable
    fun components() = remember { _components }

    @Composable
    fun add( item: Item) = _components.add { item.Draw() }

    @Composable
    fun add( component: @Composable () -> Unit) = _components.add( component )

    @Composable
    fun Draw() {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            containerColor = containerColor,
            modifier = modifier,
            content = { components().forEach { it() } }
        )
    }

    class Item(
        val iconId: Int,
        val textId: Int,
        val size: Dp = 24.dp,
        val padding: Dp = Dp.Hairline,
        val colors: MenuItemColors? = null,
        val modifier: Modifier = Modifier,
        val onClick: () -> Unit
    ) {

        companion object {

            @Composable
            fun colors(): MenuItemColors {
                return MenuItemColors(
                    leadingIconColor =  colorPalette().favoritesIcon,
                    trailingIconColor =  colorPalette().favoritesIcon,
                    textColor = colorPalette().textSecondary,
                    disabledTextColor = colorPalette().text,
                    disabledLeadingIconColor = colorPalette().text,
                    disabledTrailingIconColor = colorPalette().text,
                )
            }
        }

        @Composable
        fun Draw() {
            val icon: @Composable () -> Unit = {
                Icon(
                    painter = painterResource( iconId ),
                    contentDescription = null,
                    modifier = modifier.size( 24.dp )
                )
            }

            DropdownMenuItem(
                enabled = true,
                colors = colors ?: colors(),
                text = { Text( stringResource(textId) ) },
                leadingIcon = icon,
                onClick = onClick
            )
        }
    }
}