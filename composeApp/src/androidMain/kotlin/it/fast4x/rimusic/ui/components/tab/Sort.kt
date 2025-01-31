package it.fast4x.rimusic.ui.components.tab

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.enums.SortOrder
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.MenuState
import it.fast4x.rimusic.ui.components.themed.MenuEntry
import it.fast4x.rimusic.utils.menuStyleKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.ui.components.navigation.header.TabToolBar
import it.fast4x.rimusic.ui.components.tab.toolbar.Clickable
import it.fast4x.rimusic.ui.components.tab.toolbar.Menu
import it.fast4x.rimusic.ui.components.tab.toolbar.MenuIcon
import it.fast4x.rimusic.enums.Drawable
import it.fast4x.rimusic.enums.MenuTitle
import it.fast4x.rimusic.typography
import org.intellij.lang.annotations.MagicConstant
import kotlin.enums.EnumEntries

open class Sort<T: Enum<T>> protected constructor(
    protected val sortOrderState: MutableState<SortOrder>,
    protected val sortByEntries: EnumEntries<T>,
    protected val sortByState: MutableState<T>,
    override val menuState: MenuState,
    override val styleState: MutableState<MenuStyle>
): MenuIcon, Clickable, Menu {

    companion object {
        @JvmStatic
        @Composable
        fun <T: Enum<T>> init(
            @MagicConstant sortOrderKey: String,
            sortByEnums: EnumEntries<T>,
            sortByState: MutableState<T>
        ): Sort<T> = Sort(
                rememberPreference( sortOrderKey, SortOrder.Descending ),
                sortByEnums,
                sortByState,
                LocalMenuState.current,
                rememberPreference( menuStyleKey, MenuStyle.List )
            )
    }

    private val arrowDirection: State<Float>
        @Composable
        get() = animateFloatAsState(
            targetValue = sortOrder.rotationZ,
            animationSpec = tween(durationMillis = 400, easing = LinearEasing),
            label = ""
        )

    override val iconId: Int = R.drawable.arrow_up
    override val menuIconTitle: String
        @Composable
        // TODO: Add string "sort_item"
        get() = stringResource( R.string.sorting_order )


    var sortOrder: SortOrder = sortOrderState.value
        set(value) {
            sortOrderState.value = value
            field = value
        }
    var sortBy: T = sortByState.value
        set(value) {
            sortByState.value = value
            field = value
        }

    @Composable
    private fun <T: Enum<T>> Menu(
        onDismiss: () -> Unit,
        entries: EnumEntries<T>,
        actions: (T) -> Unit
    ) {
        Menu( entries ) {
            val icon =
                if( it is Drawable)
                    it.icon
                else
                    painterResource( R.drawable.text )

            if( it is MenuTitle)
                MenuEntry(
                    painter = icon,
                    text = stringResource( it.titleId ),
                    onClick = {
                        onDismiss()
                        actions( it )
                    }
                )
        }
    }

    @Composable
    protected fun <T: Enum<T>> Menu(
        entries: EnumEntries<T>,
        entry: @Composable ( T ) -> Unit
    ) {
        it.fast4x.rimusic.ui.components.themed.Menu {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                BasicText(
                    text = stringResource(R.string.sorting_order),
                    style = typography().m.semiBold,
                    modifier = Modifier.padding(
                        vertical = 8.dp,
                        horizontal = 24.dp
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            entries.forEach { entry(it) }
        }
    }

    @Composable
    override fun ListMenu() { /* Does nothing */ }

    @Composable
    override fun GridMenu() { /* Does nothing */ }

    @Composable
    override fun MenuComponent() {
        Menu(
            menuState::hide,
            sortByEntries,
        ) { sortBy = it }
    }

    /** Flip oder. */
    override fun onShortClick() { sortOrder = !sortOrder }

    override fun onLongClick() = menuState.display { MenuComponent() }

    @Composable
    override fun ToolBarButton() {
        val animatedArrow by arrowDirection

        TabToolBar.Icon(
            icon,
            color,
            sizeDp,
            isEnabled,
            this.modifier.graphicsLayer { rotationZ = animatedArrow },
            this::onShortClick,
            this::onLongClick
        )
    }
}