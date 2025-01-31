package it.fast4x.rimusic.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

fun Modifier.verticalFadingEdge() =
    graphicsLayer(alpha = 0.95f)
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black, Color.Black, Color.Black,
                        Color.Transparent
                    )
                ),
                blendMode = BlendMode.DstIn
            )
        }

fun Modifier.horizontalFadingEdge() =
    graphicsLayer(alpha = 0.95f)
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black, Color.Black, Color.Black,
                        Color.Transparent
                    )
                ),
                blendMode = BlendMode.DstIn
            )
        }

fun Modifier.fadingEdge(
    left: Dp? = null,
    top: Dp? = null,
    right: Dp? = null,
    bottom: Dp? = null,
) = graphicsLayer(alpha = 0.99f)
    .drawWithContent {
        drawContent()
        if (top != null) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black
                    ),
                    startY = 0f,
                    endY = top.toPx()
                ),
                blendMode = BlendMode.DstIn
            )
        }
        if (bottom != null) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color.Transparent
                    ),
                    startY = size.height - bottom.toPx(),
                    endY = size.height
                ),
                blendMode = BlendMode.DstIn
            )
        }
        if (left != null) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black,
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = left.toPx()
                ),
                blendMode = BlendMode.DstIn
            )
        }
        if (right != null) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black
                    ),
                    startX = size.width - right.toPx(),
                    endX = size.width
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }

fun Modifier.fadingEdge(
    horizontal: Dp? = null,
    vertical: Dp? = null,
) = fadingEdge(
    left = horizontal,
    right = horizontal,
    top = vertical,
    bottom = vertical
)

fun Modifier.VerticalfadingEdge2(fade: Float, showTopActionsBar: Boolean, topPadding: Boolean, expandedplayer: Boolean) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .conditional(showTopActionsBar || topPadding || expandedplayer){
    drawWithContent {
        val topFade = Brush.verticalGradient(0f to Color.Transparent, fade to Color.Red)
        drawContent()
        drawRect(brush = topFade, blendMode = BlendMode.DstIn)
       }
    }
    .drawWithContent {
        val bottomFade = Brush.verticalGradient(0f to Color.Transparent, fade to Color.Red, startY =  Float.POSITIVE_INFINITY, endY = 0f)
        drawContent()
        drawRect(brush = bottomFade, blendMode = BlendMode.DstIn)
    }

fun Modifier.HorizontalfadingEdge2(fade: Float) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
            val Fade = Brush.horizontalGradient(0f to Color.Transparent, fade to Color.Black,(1f-fade) to Color.Black,1f to Color.Transparent)
            drawContent()
            drawRect(brush = Fade, blendMode = BlendMode.DstIn)
        }