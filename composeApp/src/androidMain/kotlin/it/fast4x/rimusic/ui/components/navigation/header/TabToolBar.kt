package it.fast4x.rimusic.ui.components.navigation.header

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.ui.components.tab.toolbar.Button
import it.fast4x.rimusic.ui.components.tab.toolbar.EllipsisMenuComponent

object TabToolBar {

    val TOOLBAR_ICON_SIZE = 32.dp
    val HORIZONTAL_PADDING = 12.dp
    val VERTICAL_PADDING = 4.dp

    @Composable
    fun Buttons( buttons: List<Button> ) {
        val configuration = LocalConfiguration.current
        val availableWidth = configuration.screenWidthDp.dp - (HORIZONTAL_PADDING * 2)

        /*
            `15.dp` is a magic number used to approximate the spacing
            between each item displayed inside `Row` composable.

            Why?
            A 660.dp screen can display up to 10 items without spacing.
            But since [Arrangement.SpaceEvenly] is set, only 7 items
            can be displayed, any other item will be hidden.

            TODO: Implement a more accurate/efficient mathematical equation to calculate spacing
        */
        val sizeWithSpacing = TOOLBAR_ICON_SIZE + 15.dp
        val canDisplay = (availableWidth / sizeWithSpacing).toInt()
        val isClustered by rememberSaveable( canDisplay ) {
            mutableStateOf( buttons.size > canDisplay )
        }

        val ellipsisMenu = EllipsisMenuComponent.init {
            buttons.takeLast(
                /*
                 * Take what isn't displayed, or 0 if [canDisplay]
                 * is equal or larger than [button]'s size.
                 *
                 * Ellipsis menu will replaces last icon with its icon,
                 * therefore, `1` is added to include that last icon
                 * back to the menu
                 */
                (buttons.size - canDisplay + 1).coerceAtLeast( 0 )
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(HORIZONTAL_PADDING, VERTICAL_PADDING)
        ) {

            buttons.take(
                if( isClustered )
                // Last item is reserved for ellipsis menu's icon
                    canDisplay - 1
                else
                    buttons.size
            ).forEach { it.ToolBarButton() }

            if( isClustered )
                ellipsisMenu.ToolBarButton()
        }
    }

    @Composable
    fun Buttons( vararg buttons: Button) = Buttons( listOf( *buttons ) )

    @Composable
    fun Icon(
        icon: Painter,
        tint: Color = colorPalette().text,
        size: Dp = TOOLBAR_ICON_SIZE,
        enabled: Boolean = true,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {}
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            androidx.compose.material3.Icon(
                painter = icon,
                null,
                modifier.size( size )
                        .padding( horizontal = 4.dp ),
                tint
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Icon(
        icon: Painter,
        tint: Color = colorPalette().text,
        size: Dp = TOOLBAR_ICON_SIZE,
        enabled: Boolean = true,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
        onLongClick: () -> Unit = {}
    ) {
        Icon(
            icon,
            tint,
            size,
            enabled,
            modifier.combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        )
    }

    @Composable
    fun Icon(
        @DrawableRes iconId: Int,
        tint: Color = colorPalette().text,
        size: Dp = TOOLBAR_ICON_SIZE,
        enabled: Boolean = true,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource( iconId ),
                tint = tint,
                contentDescription = null,
                modifier = modifier
                    .size(size)
                    .padding(horizontal = 4.dp)
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Icon(
        @DrawableRes iconId: Int,
        tint: Color = colorPalette().text,
        size: Dp = TOOLBAR_ICON_SIZE,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        onShortClick: () -> Unit,
        onLongClick: () -> Unit
    ) {
        Icon(
            iconId,
            tint,
            size,
            enabled,
            modifier.combinedClickable (
                onClick = onShortClick,
                onLongClick = onLongClick
            )
        ) { }
    }

    @Composable
    fun Toggleable(
        @DrawableRes onIconId: Int,
        @DrawableRes offIconId: Int,
        toggleCondition: Boolean,
        tint: Color = colorPalette().text,
        size: Dp = TOOLBAR_ICON_SIZE,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Icon(
            iconId = if( toggleCondition ) onIconId else offIconId,
            tint = tint,
            size = size,
            modifier = modifier,
            onClick = onClick
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Toggleable(
        @DrawableRes onIconId: Int,
        @DrawableRes offIconId: Int,
        toggleCondition: Boolean,
        tint: Color = colorPalette().text,
        size: Dp = TOOLBAR_ICON_SIZE,
        modifier: Modifier = Modifier,
        onShortClick: () -> Unit,
        onLongClick: () -> Unit
    ) {
        Toggleable(
            onIconId,
            offIconId,
            toggleCondition,
            tint,
            size,
            modifier.combinedClickable (
                onClick = onShortClick,
                onLongClick = onLongClick
            )
        ) { }
    }

    @Composable
    fun Toggleable(
        @DrawableRes iconId: Int,
        tintOn: Color = colorPalette().text,
        tintOff: Color = colorPalette().textDisabled,
        toggleCondition: Boolean,
        enabled: Boolean,
        size: Dp = TOOLBAR_ICON_SIZE,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Icon(
            iconId = iconId,
            tint = if( toggleCondition ) tintOn else tintOff,
            size = size,
            enabled = enabled,
            modifier = modifier,
            onClick = onClick
        )
    }

    @ExperimentalFoundationApi
    @Composable
    fun Toggleable(
        @DrawableRes iconId: Int,
        tintOn: Color = colorPalette().text,
        tintOff: Color = colorPalette().textDisabled,
        toggleCondition: Boolean,
        enabled: Boolean,
        size: Dp = TOOLBAR_ICON_SIZE,
        modifier: Modifier = Modifier,
        onShortClick: () -> Unit,
        onLongClick: () -> Unit
    ) {
        Toggleable(
            iconId,
            tintOn,
            tintOff,
            toggleCondition,
            enabled,
            size,
            modifier.combinedClickable (
                onClick = onShortClick,
                onLongClick = onLongClick
            )
        ) { }
    }
}