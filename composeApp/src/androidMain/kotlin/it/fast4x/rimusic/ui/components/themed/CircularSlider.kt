package it.fast4x.rimusic.ui.components.themed

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import it.fast4x.rimusic.colorPalette
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@ExperimentalTextApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CircularSlider(
    modifier: Modifier = Modifier,
    padding: Float = 50f,
    stroke: Float = 20f,
    cap: StrokeCap = StrokeCap.Round,
    touchStroke: Float = 50f,
    thumbColor: Color = Color.Blue,
    progressColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray,
    debug: Boolean = false,
    text: String = "",
    onChange: ((Float)->Unit)? = null
){
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var angle by remember { mutableStateOf(-60f) }
    var last by remember { mutableStateOf(0f) }
    var down  by remember { mutableStateOf(false) }
    var radius by remember { mutableStateOf(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }
    var appliedAngle by remember { mutableStateOf(0f) }

    val textMeasurer = rememberTextMeasurer()
    val style = TextStyle(
        fontSize = 25.sp,
        color = colorPalette().text,
        background = colorPalette().background4
    )
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(AnnotatedString(text), style)
    }

    LaunchedEffect(key1 = angle){
        var a = angle
        a += 60
        if(a<=0f){
            a += 360
        }
        a = a.coerceIn(0f,300f)
        if(last<150f&&a==300f){
            a = 0f
        }
        last = a
        appliedAngle = a
    }
    LaunchedEffect(key1 = appliedAngle){
        onChange?.invoke(appliedAngle/300f)
    }
    Canvas(
        modifier = modifier
            .onGloballyPositioned {
                width = it.size.width
                height = it.size.height
                center = Offset(width / 2f, height / 2f)
                radius = min(width.toFloat(), height.toFloat()) / 2f - padding - stroke/2f
            }
            .pointerInteropFilter {
                val x = it.x
                val y = it.y
                val offset = Offset(x, y)
                when (it.action) {

                    MotionEvent.ACTION_DOWN -> {
                        val d = distance(offset, center)
                        val a = angle(center, offset)
                        if (d >= radius - touchStroke / 2f && d <= radius + touchStroke / 2f && a !in -120f..-60f) {
                            down = true
                            angle = a
                        } else {
                            down = false
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (down) {
                            angle = angle(center, offset)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        down = false
                    }
                    else -> return@pointerInteropFilter false
                }
                return@pointerInteropFilter true
            }
    ){

        drawArc(
            color = backgroundColor,
            startAngle = -240f,
            sweepAngle = 300f,
            topLeft = center - Offset(radius,radius),
            size = Size(radius*2,radius*2),
            useCenter = false,
            style = Stroke(
                width = stroke,
                cap = cap
            )
        )
        drawArc(
            color = progressColor,
            startAngle = 120f,
            sweepAngle = appliedAngle,
            topLeft = center - Offset(radius,radius),
            size = Size(radius*2,radius*2),
            useCenter = false,
            style = Stroke(
                width = stroke,
                cap = cap
            )
        )
        drawCircle(
            color = thumbColor,
            radius = stroke,
            center = center + Offset(
                radius*cos((120+appliedAngle)*PI/180f).toFloat(),
                radius*sin((120+appliedAngle)*PI/180f).toFloat()
            )
        )

        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style,
            topLeft = Offset(
                x = center.x - textLayoutResult.size.width / 2,
                y = center.y - textLayoutResult.size.height / 2,
            )
        )

        if(debug){
            drawRect(
                color = Color.Green,
                topLeft = Offset.Zero,
                size = Size(width.toFloat(),height.toFloat()),
                style = Stroke(
                    4f
                )
            )
            drawRect(
                color = Color.Red,
                topLeft = Offset(padding,padding),
                size = Size(width.toFloat()-padding*2,height.toFloat()-padding*2),
                style = Stroke(
                    4f
                )
            )
            drawRect(
                color = Color.Blue,
                topLeft = Offset(padding,padding),
                size = Size(width.toFloat()-padding*2,height.toFloat()-padding*2),
                style = Stroke(
                    4f
                )
            )
            drawCircle(
                color = Color.Red,
                center = center,
                radius = radius+stroke/2f,
                style = Stroke(2f)
            )
            drawCircle(
                color = Color.Red,
                center = center,
                radius = radius-stroke/2f,
                style = Stroke(2f)
            )
        }
    }
}

fun angle(center: Offset, offset: Offset): Float {
    val rad = atan2(center.y - offset.y, center.x - offset.x)
    val deg = Math.toDegrees(rad.toDouble())
    return deg.toFloat()
}
fun distance(first: Offset, second: Offset) : Float{
    return sqrt((first.x-second.x).square()+(first.y-second.y).square())
}
fun Float.square(): Float{
    return this*this
}