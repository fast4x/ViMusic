package it.fast4x.rimusic.ui.components.themed

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.ui.styling.LocalAppearance

@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap
) {
    val (colorPalette) = LocalAppearance.current

    LinearProgressIndicator(
        modifier = modifier,
        color = colorPalette.text,
        trackColor = colorPalette.textDisabled,
        strokeCap = strokeCap,
        progress = { progress!! },
        gapSize = 2.dp
    )
}

@Composable
fun ProgressIndicatorCircular(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
) {
    val (colorPalette) = LocalAppearance.current

   CircularProgressIndicator(
        modifier = modifier,
        color = colorPalette.text,
        trackColor = colorPalette.textDisabled,
        strokeCap = strokeCap,
        progress = { progress!! },
        gapSize = 2.dp
   )
}