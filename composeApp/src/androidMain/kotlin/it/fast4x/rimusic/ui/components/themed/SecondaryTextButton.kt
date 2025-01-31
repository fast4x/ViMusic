package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.ui.styling.primaryButton
import it.fast4x.rimusic.utils.medium
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography

@Composable
fun SecondaryTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alternative: Boolean = false
) {
    BasicText(
        text = text,
        style = typography().xxs.medium,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .background(if (alternative) colorPalette().background0 else colorPalette().primaryButton)
            .padding(all = 8.dp)
            .padding(horizontal = 8.dp)

    )
}
