package it.fast4x.rimusic.utils

import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.colorPalette

@Composable
fun menuItemColors(): MenuItemColors {
    return MenuItemColors(
        leadingIconColor =  colorPalette().favoritesIcon,
        trailingIconColor =  colorPalette().favoritesIcon,
        textColor = colorPalette().textSecondary,
        disabledTextColor = colorPalette().text,
        disabledLeadingIconColor = colorPalette().text,
        disabledTrailingIconColor = colorPalette().text,
    )

}