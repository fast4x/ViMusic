package it.fast4x.rimusic.ui.components.themed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.ThumbnailCoverType
import it.fast4x.rimusic.utils.VinylSizeKey
import it.fast4x.rimusic.utils.rememberPreference

@Composable
fun RotateThumbnailCover(
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
    painter: Painter,
    type: ThumbnailCoverType = ThumbnailCoverType.Vinyl,
    imageCoverSize : Float = 50f
) {
    val roundedShape = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val p1 = Path().apply {
                addOval(Rect(4f, 3f, size.width - 1, size.height - 1))
            }
            val thickness = size.height / 2.10f
            val p2 = Path().apply {
                addOval(
                    Rect(
                        thickness,
                        thickness,
                        size.width - thickness,
                        size.height - thickness
                    )
                )
            }
            val p3 = Path()
            p3.op(p1, p2, PathOperation.Difference)

            return Outline.Generic(p1)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1.0f)
            .clip(roundedShape)
    ) {

            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotationDegrees)
                    .aspectRatio(1.0f),
                painter = painterResource(id = when (type) {
                    ThumbnailCoverType.Vinyl -> R.drawable.vinyl_background
                    ThumbnailCoverType.CD, ThumbnailCoverType.CDwithCover -> R.drawable.cd
                }),
                contentDescription = "disc background"
            )

        if (type in listOf(ThumbnailCoverType.Vinyl, ThumbnailCoverType.CDwithCover))
            Image(
                modifier = Modifier
                    .fillMaxSize(
                        when (type) {
                            ThumbnailCoverType.Vinyl -> imageCoverSize * 0.01f
                            ThumbnailCoverType.CD -> 1f
                            ThumbnailCoverType.CDwithCover -> 0.85f
                        }

                    )
                    .rotate(rotationDegrees)
                    .aspectRatio(1.0f)
                    .align(Alignment.Center)
                    .clip(roundedShape),
                painter = painter,
                contentDescription = "song album cover"
            )

        if (type == ThumbnailCoverType.CDwithCover) {
            Box(
                modifier = Modifier
                    .clip(roundedShape)
                    .fillMaxSize(0.3f)
                    .border(BorderStroke(2.dp, Color.LightGray), roundedShape)
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .clip(roundedShape)
                    .fillMaxSize(0.2f)
                    .background(Color.LightGray)
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .clip(roundedShape)
                    .fillMaxSize(0.1f)
                    .background(Color.Black)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun RotateThumbnailCoverAnimation(
    modifier: Modifier = Modifier,
    isSongPlaying: Boolean = true,
    painter: Painter,
    type: ThumbnailCoverType = ThumbnailCoverType.Vinyl
) {
    var currentRotation by remember {
        mutableFloatStateOf(0f)
    }

    val rotation = remember {
        Animatable(currentRotation)
    }

    LaunchedEffect(isSongPlaying) {
        if (isSongPlaying) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = value
            }
        } else {
            if (currentRotation > 0f) {
                rotation.animateTo(
                    targetValue = currentRotation + 50,
                    animationSpec = tween(
                        1250,
                        easing = LinearOutSlowInEasing
                    )
                ) {
                    currentRotation = value
                }
            }
        }
    }

    RotateThumbnailCover(
        painter = painter,
        rotationDegrees = rotation.value,
        modifier = modifier,
        type = type
    )
}

@Composable
fun RotateThumbnailCoverAnimationModern(
    modifier: Modifier = Modifier,
    type: ThumbnailCoverType = ThumbnailCoverType.Vinyl,
    isSongPlaying: Boolean = true,
    painter: Painter,
    state : PagerState,
    it : Int,
    imageCoverSize : Float
) {
    var currentRotation by remember {
        mutableFloatStateOf(0f)
    }

    val rotation = remember {
        Animatable(currentRotation)
    }

    LaunchedEffect(isSongPlaying, state.settledPage) {
        if (isSongPlaying && it == state.settledPage) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = value
            }
        } else {
            if (currentRotation > 0f && it == state.settledPage) {
                rotation.animateTo(
                    targetValue = currentRotation + 50,
                    animationSpec = tween(
                        1250,
                        easing = LinearOutSlowInEasing
                    )
                ) {
                    currentRotation = value
                }
            }
        }
    }

    RotateThumbnailCover(
        painter = painter,
        rotationDegrees = rotation.value,
        modifier = modifier,
        type = type,
        imageCoverSize = imageCoverSize
    )
}