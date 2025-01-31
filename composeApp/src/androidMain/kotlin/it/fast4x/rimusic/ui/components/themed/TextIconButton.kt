package it.fast4x.rimusic.ui.components.themed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.typography

class TextIconButton(
    val text: String,
    iconId: Int,
    color: Color,
    padding: Dp,
    size: Dp,
    forceWidth: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier
): Button( iconId, color, padding, size, forceWidth, modifier ) {

    @Composable
    override fun Draw() {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding( all = 5.dp )
                               .fillMaxSize()
        ){
            super.Draw()
            Spacer( modifier = Modifier.height( 5.dp ) )
            BasicText(
                text = text,
                style =  TextStyle(
                    fontSize = typography().xs.semiBold.fontSize,
                    fontWeight = typography().xs.semiBold.fontWeight,
                    fontFamily = typography().xs.semiBold.fontFamily,
                    color = color,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}