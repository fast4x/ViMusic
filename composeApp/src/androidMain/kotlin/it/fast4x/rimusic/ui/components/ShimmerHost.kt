package it.fast4x.rimusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset // Add this import
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay

@Composable
fun ShimmerHost(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    var shimmerTranslateAnim by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            shimmerTranslateAnim += 10f
            if (shimmerTranslateAnim > 1000f) {
                shimmerTranslateAnim = 0f
            }
            delay(16) // approximately 60 FPS
        }
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        modifier = modifier
            .shimmer()
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Black, Color.Transparent),
                        start = Offset(shimmerTranslateAnim, 0f),
                        end = Offset(shimmerTranslateAnim + 200f, 0f)
                    ),
                    blendMode = BlendMode.DstIn
                )
            },
        content = content
    )
}