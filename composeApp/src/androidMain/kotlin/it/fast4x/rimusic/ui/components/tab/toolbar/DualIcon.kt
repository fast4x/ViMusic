package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

/**
 * The [DualIcon] is a versatile UI component that can display
 * two different icons based on a specific state or condition.
 *
 * This feature is ideal for buttons that need to convey different
 * meanings or actions visually, such as toggling between "play/pause" or "on/off."
 */
interface DualIcon: Icon {

    @get:DrawableRes
    val secondIconId: Int

    /**
     * The condition determines which icon to display.
     *
     * When `true`, [iconId] will be used to display
     * icon, otherwise, [secondIconId] will be used
     */
    var isFirstIcon: Boolean

    override val icon: Painter
        @Composable
        get() = if( isFirstIcon ) super.icon else painterResource( secondIconId )
}