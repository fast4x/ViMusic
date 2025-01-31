package it.fast4x.rimusic.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.enums.PlayerTimelineType
import kotlin.math.roundToLong

@Composable
fun SeekBarCustom(
    type: PlayerTimelineType,
    value: Long,
    minimumValue: Long,
    maximumValue: Long,
    onDragStart: (Long) -> Unit,
    onDrag: (Long) -> Unit,
    onDragEnd: () -> Unit,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    barHeight: Dp = 20.dp,
    scrubberColor: Color = color,
    scrubberRadius: Dp = 2.dp,
    shape: Shape = RectangleShape,
    drawSteps: Boolean = false,
) {
    var _barHeight = barHeight
    var _backbarHeight = barHeight
    var _scrubberRadius = scrubberRadius

    when (type) {
        PlayerTimelineType.PinBar -> {
            _barHeight = 15.dp
            _backbarHeight = 3.dp
            _scrubberRadius = 0.dp
        }
        PlayerTimelineType.BodiedBar -> {
            _barHeight = 15.dp
            _backbarHeight = 15.dp
            _scrubberRadius = 0.dp
        }
        else -> {
            _barHeight = 3.dp
            _backbarHeight = 3.dp
            _scrubberRadius = 6.dp
        }
    }



    val isDragging = remember {
        MutableTransitionState(false)
    }

    val transition = rememberTransition(transitionState = isDragging, label = null)

    val currentBarHeight by transition.animateDp(label = "") { if (it) _scrubberRadius else _barHeight }
    val currentScrubberRadius by transition.animateDp(label = "") { if (it) 8.dp else _scrubberRadius }

    Box(
        modifier = modifier
            .pointerInput(minimumValue, maximumValue) {
                if (maximumValue < minimumValue) return@pointerInput

                var acc = 0f

                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging.targetState = true
                    },
                    onHorizontalDrag = { _, delta ->
                        acc += delta / size.width * (maximumValue - minimumValue)

                        if (acc !in -1f..1f) {
                            onDrag(acc.toLong())
                            acc -= acc.toLong()
                        }
                    },
                    onDragEnd = {
                        isDragging.targetState = false
                        acc = 0f
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging.targetState = false
                        acc = 0f
                        onDragEnd()
                    }
                )
            }
            .pointerInput(minimumValue, maximumValue) {
                if (maximumValue < minimumValue) return@pointerInput

                detectTapGestures(
                    onPress = { offset ->
                        onDragStart((offset.x / size.width * (maximumValue - minimumValue) + minimumValue).roundToLong())
                    },
                    onTap = {
                        onDragEnd()
                    }
                )
            }
            .padding(horizontal = scrubberRadius)
            .drawWithContent {
                drawContent()

                val scrubberPosition = if (maximumValue < minimumValue) {
                    0f
                } else {
                    (value.toFloat() - minimumValue) / (maximumValue - minimumValue) * size.width
                }

                drawCircle(
                    color = scrubberColor,
                    radius = currentScrubberRadius.toPx(),
                    center = center.copy(x = scrubberPosition)
                )

                if (drawSteps) {
                    for (i in value + 1..maximumValue) {
                        val stepPosition =
                            (i.toFloat() - minimumValue) / (maximumValue - minimumValue) * size.width
                        drawCircle(
                            color = scrubberColor,
                            radius = scrubberRadius.toPx() / 2,
                            center = center.copy(x = stepPosition),
                        )
                    }
                }
            }
            //.height(scrubberRadius)
    ) {

        Spacer(
            modifier = Modifier
                //.height(currentBarHeight)
                .height(_backbarHeight)
                .fillMaxWidth()
                .background(color = backgroundColor, shape = shape)
                .align(Alignment.Center)
        )

        Spacer(
            modifier = Modifier
                .height(currentBarHeight)
                .fillMaxWidth((value.toFloat() - minimumValue) / (maximumValue - minimumValue))
                .background(color = color, shape = shape)
                .align(Alignment.CenterStart)
        )
    }
}
