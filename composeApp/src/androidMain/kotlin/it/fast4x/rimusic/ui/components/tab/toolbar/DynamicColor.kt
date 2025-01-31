package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import it.fast4x.rimusic.colorPalette

/**
 * The [DynamicColor] is a UI component designed to
 * dynamically change its color based on a specific condition.
 *
 * This button alternates between two predefined colors,
 * providing a more robust visual feedback to the user according
 * to its state or an external condition.
 */
interface DynamicColor: Icon {

    /**
     * Second color of this icon.
     *
     * By default, [it.fast4x.rimusic.ui.styling.ColorPalette.textDisabled] is used.
     */
    val secondColor: Color
        @Composable
        get() = colorPalette().textDisabled

    /**
     * The condition determines which color to display.
     *
     * When `true`, [color] will be used to display
     * icon, otherwise, [secondColor] will be used.
     */
    var isFirstColor: Boolean

    override val color: Color
        @Composable
        get() = if( isFirstColor ) super.color else secondColor
}