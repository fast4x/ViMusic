package it.fast4x.rimusic.ui.components


import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.lerp
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random
import androidx.annotation.FloatRange
import androidx.compose.ui.unit.Dp

private const val waveWidthPercentOfSpaceAvailable = 0.5f

@Composable
fun SeekBarAudioWaves(
    progressPercentage: ProgressPercentage,
    playedColor: Color,
    notPlayedColor: Color,
    waveInteraction: WaveInteraction,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val updatedWaveInteraction by rememberUpdatedState(waveInteraction)
        val numberOfWaves = remember(maxWidth) {
            (maxWidth / 3f).value.roundToInt() //5f default
        }
        val waveWidth = remember(maxWidth) {
            (maxWidth / numberOfWaves.toFloat()) * waveWidthPercentOfSpaceAvailable
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        updatedWaveInteraction.onInteraction(
                            ProgressPercentage.of(current = offset.x.toDp(), target = maxWidth),
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change: PointerInputChange, dragAmount: Float ->
                        // Do not trigger on minuscule movements
                        if (dragAmount.absoluteValue < 1f) return@detectHorizontalDragGestures
                        updatedWaveInteraction.onInteraction(
                            ProgressPercentage.of(
                                current = change.position.x.toDp(),
                                target = maxWidth
                            ),
                        )
                    }
                },
        ) {
            repeat(numberOfWaves) { waveIndex ->
                FakeAudioWavePill(
                    progressPercentage = progressPercentage,
                    numberOfWaves = numberOfWaves,
                    waveIndex = waveIndex,
                    playedColor = playedColor,
                    notPlayedColor = notPlayedColor,
                    modifier = Modifier.width(waveWidth),
                )
            }
        }
    }
}

private const val minWaveHeightFraction = 0.1f
private const val maxWaveHeightFractionForSideWaves = 0.1f
private const val maxWaveHeightFraction = 1.0f

@Composable
private fun FakeAudioWavePill(
    progressPercentage: ProgressPercentage,
    numberOfWaves: Int,
    waveIndex: Int,
    playedColor: Color,
    notPlayedColor: Color,
    modifier: Modifier = Modifier,
) {
    val height = remember(waveIndex, numberOfWaves) {
        val wavePosition = waveIndex + 1
        val centerPoint = numberOfWaves / 2
        val distanceFromCenterPoint = abs(centerPoint - wavePosition)
        val percentageToCenterPoint = ((centerPoint - distanceFromCenterPoint).toFloat() / centerPoint)
        val maxHeightFraction = lerp(
            maxWaveHeightFractionForSideWaves,
            maxWaveHeightFraction,
            percentageToCenterPoint,
        )
        val validMaxHeightFraction = if (maxHeightFraction.isNaN()) 0.1f else maxHeightFraction
        if (validMaxHeightFraction <= minWaveHeightFraction) {
            validMaxHeightFraction
        } else {
            Random.nextDouble(minWaveHeightFraction.toDouble(), validMaxHeightFraction.toDouble()).toFloat()
        }
    }
    val hasPlayedThisWave = remember(progressPercentage, numberOfWaves, waveIndex) {
        progressPercentage.value * numberOfWaves > waveIndex
    }
    Surface(
        shape = CircleShape,
        color = if (hasPlayedThisWave) playedColor else notPlayedColor,
        modifier = modifier.fillMaxHeight(fraction = height),
    ) {}
}


@JvmInline
value class ProgressPercentage(
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = true, toInclusive = true)
    val value: Float,
) {
    init {
        require(value in 0.0f..1.0f) {
            "Progress percentage must be within 0.0f inclusive to 1.0f inclusive. Value: $value"
        }
    }

    val isDone: Boolean
        get() = value == 1f

    companion object {
        fun safeValue(float: Float): ProgressPercentage {
            if (float.isNaN()) return ProgressPercentage(0f)
            return ProgressPercentage(float.coerceIn(0f, 1f))
        }

        fun of(
            current: Dp,
            target: Dp,
        ): ProgressPercentage {
            return ProgressPercentage(
                (current / target).coerceIn(0f, 1f),
            )
        }
    }
}

fun interface WaveInteraction {
    /**
     * [horizontalProgressPercentage] is a value that shows where in the horizontal spectrum the wave was interacted
     * with.
     * Ranges from 0.0f when interacted on the far left to 1.0f on the far right.
     */
    fun onInteraction(horizontalProgressPercentage: ProgressPercentage)
}