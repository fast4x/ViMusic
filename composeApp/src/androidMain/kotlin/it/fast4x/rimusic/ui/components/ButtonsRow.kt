package it.fast4x.rimusic.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.utils.colorPaletteModeKey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.colorPalette

@Composable
fun <E> ButtonsRow(
    chips: List<Pair<E, String>>,
    currentValue: E,
    onValueUpdate: (E) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorPaletteMode by rememberPreference(colorPaletteModeKey, ColorPaletteMode.Dark)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.width(12.dp))

        chips.forEach { (value, label) ->
            FilterChip(
                label = { Text(label) },
                selected = currentValue == value,
                colors = FilterChipDefaults
                    .filterChipColors(
                        containerColor = colorPalette().background1,
                        labelColor = colorPalette().text,
                        selectedContainerColor = when (colorPaletteMode) {
                            ColorPaletteMode.Dark, ColorPaletteMode.PitchBlack
                                -> colorPalette().textDisabled
                            else -> colorPalette().background3
                        } ,
                        selectedLabelColor = colorPalette().text,
                    ),
                onClick = { onValueUpdate(value) }
            )

            Spacer(Modifier.width(8.dp))
        }
    }
}
