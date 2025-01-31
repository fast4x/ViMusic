package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

open class Button(
    val iconId: Int,
    var color: Color,
    var padding: Dp,
    var size: Dp,
    var forceWidth: Dp = Dp.Unspecified,
    var modifier: Modifier = Modifier
) {
    fun modifier( newModifier: (Modifier) -> Modifier ) {
        this.modifier = newModifier( this.modifier )
    }

    @Composable
    open fun Draw() {
        Image(
            painter = painterResource( iconId ),
            contentDescription = null,
            colorFilter = ColorFilter.tint( color ),
            modifier = modifier.padding( all = padding )
                               .size( height = size, width = if ( forceWidth == Dp.Unspecified ) size else forceWidth )
        )
    }
}
