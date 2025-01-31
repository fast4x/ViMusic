package it.fast4x.rimusic.ui.components.tab.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.ui.components.themed.GridMenuItemHeight
import it.fast4x.rimusic.ui.components.themed.MenuEntry

interface MenuIcon: Icon {

    @get:Composable
    val menuIconTitle: String

    @Composable
    fun GridTextComponent() {
        Text(
            text = menuIconTitle,
            overflow = TextOverflow.Ellipsis,
            color = color,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun BoxScope.GridIconComponent() = ToolBarButton()

    @Composable
    fun GridMenuItem() {
        Column(
            modifier = modifier
                .clip(ShapeDefaults.Large)
                .height(GridMenuItemHeight)
                .alpha(if (isEnabled) 1f else 0.5f)
                .padding(12.dp)
                .clickable(
                    enabled = isEnabled,
                    onClick = ::onShortClick
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
                content = { GridIconComponent() }
            )
            GridTextComponent()
        }
    }

    @Composable
    fun ListMenuItem() {
        MenuEntry(
            icon,
            menuIconTitle,
            ::onShortClick,
        )
    }
}