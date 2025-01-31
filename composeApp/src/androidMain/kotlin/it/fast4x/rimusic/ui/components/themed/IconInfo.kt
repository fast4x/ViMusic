package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography

@Composable
fun IconInfo (
    title: String,
    icon: Painter,
    spacer: Dp = 4.dp,
    iconSize: Dp = 20.dp
) {
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorPalette().text),
            modifier = Modifier
                .size(iconSize)
        )
        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
        BasicText(
            text = title,
            style = TextStyle(
                color = colorPalette().text,
                fontStyle = typography().l.fontStyle
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

    }
}