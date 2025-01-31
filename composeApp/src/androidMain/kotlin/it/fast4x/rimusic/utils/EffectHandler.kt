package it.fast4x.rimusic.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import it.fast4x.rimusic.enums.TransitionEffect

private val tween350 = tween<Float>( 350 )

private fun slideDirection(
    transitionEffect: TransitionEffect,
    targetState: Int,
    initialState: Int
): AnimatedContentTransitionScope.SlideDirection {
    val isSlideHorizontal = transitionEffect == TransitionEffect.SlideHorizontal

    return when ( targetState > initialState ) {
        true ->
            if ( isSlideHorizontal )
                AnimatedContentTransitionScope.SlideDirection.Left
            else
                AnimatedContentTransitionScope.SlideDirection.Up
        false ->
            if ( isSlideHorizontal )
                AnimatedContentTransitionScope.SlideDirection.Right
            else
                AnimatedContentTransitionScope.SlideDirection.Down
    }
}

private fun scale(): ContentTransform = scaleIn( tween350 ) togetherWith scaleOut( tween350 )

private fun fade(): ContentTransform = fadeIn( tween350 ) togetherWith fadeOut( tween350 )

private fun expand(): ContentTransform {
    val expandIn = expandIn(
        tween( 350, 0, LinearOutSlowInEasing ),
        Alignment.TopStart
    )
    val shrinkOut = shrinkOut(
        tween( 350, 0, LinearOutSlowInEasing ),
        Alignment.TopStart
    )
    return expandIn togetherWith shrinkOut
}

private fun none(): ContentTransform = EnterTransition.None togetherWith ExitTransition.None

@Composable
fun transition(): AnimatedContentTransitionScope<Int>.() -> ContentTransform {

    val transitionEffect by rememberPreference(transitionEffectKey, TransitionEffect.Scale)

    return {
        when( transitionEffect ) {
            TransitionEffect.Scale -> scale()
            TransitionEffect.Fade -> fade()
            TransitionEffect.Expand -> expand()
            TransitionEffect.None -> none()
            TransitionEffect.SlideVertical, TransitionEffect.SlideHorizontal -> {
                val animationSpec = spring(
                    dampingRatio = 0.9f,
                    stiffness = Spring.StiffnessLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
                val slideDirection = slideDirection( transitionEffect, targetState, initialState )
                slideIntoContainer(slideDirection, animationSpec) togetherWith slideOutOfContainer(slideDirection, animationSpec)
            }
        }
    }
}