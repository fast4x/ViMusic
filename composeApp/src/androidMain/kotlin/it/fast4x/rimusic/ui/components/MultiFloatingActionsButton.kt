package it.fast4x.rimusic.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import it.fast4x.rimusic.R
import it.fast4x.rimusic.ui.styling.favoritesIcon
import it.fast4x.rimusic.utils.multiFloatActionIconOffsetXkey
import it.fast4x.rimusic.utils.multiFloatActionIconOffsetYkey
import it.fast4x.rimusic.utils.rememberPreference
import it.fast4x.rimusic.colorPalette

enum class MultiFabState {
    Collapsed, Expanded
}
class FabItem(
    val icon: Painter,
    val label: String,
    val onFabItemClicked: () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiFloatingActionsButton (
    useAsActionsMenu: Boolean = false,
    fabIcon: Painter,
    items: List<FabItem>,
    showLabels: Boolean = true,
    onStateChanged: ((state: MultiFabState) -> Unit)? = null,
    onClick: () -> Unit
) {
    var currentState by remember { mutableStateOf(MultiFabState.Collapsed) }
    val stateTransition: Transition<MultiFabState> =
        updateTransition(targetState = currentState, label = "")
    val stateChange: () -> Unit = {
        currentState = if (stateTransition.currentState == MultiFabState.Expanded) {
            MultiFabState.Collapsed
        } else MultiFabState.Expanded
        onStateChanged?.invoke(currentState)
    }
    val rotation: Float by stateTransition.animateFloat(
        transitionSpec = {
            if (targetState == MultiFabState.Expanded) {
                spring(stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = ""
    ) { state ->
        if (state == MultiFabState.Expanded) 45f else 0f
    }
    val isEnable = currentState == MultiFabState.Expanded

    BackHandler(isEnable) {
        currentState = MultiFabState.Collapsed
    }

    val modifier = if (currentState == MultiFabState.Expanded)
        Modifier
            .fillMaxSize()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                currentState = MultiFabState.Collapsed
            } else Modifier.fillMaxSize()

    //var offsetX by remember { mutableStateOf(0f) }
    //var offsetY by remember { mutableStateOf(0f) }

    var offsetX = rememberPreference(multiFloatActionIconOffsetXkey, 0F )
    var offsetY = rememberPreference(multiFloatActionIconOffsetYkey, 0F )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            if (currentState == MultiFabState.Expanded) {
                val color = colorPalette().favoritesIcon.copy(0.85f)
                Canvas(modifier = Modifier
                    //.border(BorderStroke(1.dp, Color.Green))
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 2.2f
                        scaleY = 2.1f
                    }) {
                    translate(150f, top = 300f) {
                        scale(5f) {}
                        drawCircle( color, radius = 200.dp.toPx() )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                items.forEach { item ->
                    SmallFloatingActionButtonRow(
                        item = item,
                        stateTransition = stateTransition,
                        showLabel = showLabels
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Box(
                    modifier = Modifier
                        .offset {
                            //IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                            IntOffset(offsetX.value.toInt(), offsetY.value.toInt())
                        }
                        .pointerInput(Unit) {
                            /*
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                             */
                            detectDragGesturesAfterLongPress { change, dragAmount ->
                                change.consume()
                                offsetX.value += dragAmount.x
                                offsetY.value += dragAmount.y

                            }
                        }
                        .clip(RoundedCornerShape(16.dp))
                        //.background(colorPalette().favoritesIcon)
                        .background(colorPalette().background2)
                        //.padding(all = 20.dp)
                        //.padding(horizontal = 20.dp)
                        .height(64.dp)
                        .width(64.dp)
                        .combinedClickable(
                            onClick = {
                                if (!useAsActionsMenu)
                                    if (currentState == MultiFabState.Collapsed) onClick() else stateChange()
                                else stateChange()
                            },
                            //onDoubleClick = { stateChange() },
                            //onLongClick = { stateChange() }
                        )


                ) {
                    /*
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopEnd)
                            //.border(BorderStroke(1.dp, Color.Green))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.settings),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colorPalette().text),
                            modifier = Modifier
                                .padding(top = 5.dp, end = 5.dp)
                                .rotate(rotation)
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                        )
                    }
                    */
                    Image(
                        painter = if (!useAsActionsMenu) fabIcon else painterResource(R.drawable.menu),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette().text),
                        modifier = Modifier
                            .rotate(rotation)
                            .align(Alignment.Center)
                            .size(24.dp)
                    )
                }
            }

        }
    }

    }


@Composable
fun SmallFloatingActionButtonRow(
    item: FabItem,
    showLabel: Boolean,
    stateTransition: Transition<MultiFabState>
) {
    val alpha: Float by stateTransition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 50)
        }, label = ""
    ) { state ->
        if (state == MultiFabState.Expanded) 1f else 0f
    }
    val scale: Float by stateTransition.animateFloat(
        label = ""
    ) { state ->
        if (state == MultiFabState.Expanded) 1.0f else 0f
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .alpha(animateFloatAsState((alpha), label = "").value)
            .scale(animateFloatAsState(targetValue = scale, label = "").value)
    ) {
        if (showLabel) {
            Text(
                text = item.label,
                modifier = Modifier
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                    .clickable(onClick = { item.onFabItemClicked() })
            )
        }
        SmallFloatingActionButton(
            shape = CircleShape,
            modifier = Modifier
                .padding(4.dp),
            onClick = { item.onFabItemClicked() },
            containerColor = colorPalette().background2,
            contentColor = colorPalette().favoritesIcon
        ) {
            Icon(
                painter = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
