package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@FunctionalInterface
interface Drawable {

    val icon: Painter
        @Composable
        get
}