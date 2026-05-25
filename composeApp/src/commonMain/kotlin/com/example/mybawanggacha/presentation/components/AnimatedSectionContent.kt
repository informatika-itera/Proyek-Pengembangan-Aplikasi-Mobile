package com.example.mybawanggacha.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> AnimatedSectionContent(
    targetState: T,
    indexOf: (T) -> Int,
    modifier: Modifier = Modifier,
    label: String = "AnimatedSectionContent",
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            val forward = indexOf(targetState) >= indexOf(initialState)
            val enterOffset: (Int) -> Int = { height ->
                if (forward) height / 4 else -height / 4
            }
            val exitOffset: (Int) -> Int = { height ->
                if (forward) -height / 4 else height / 4
            }

            (slideInVertically(
                animationSpec = tween(durationMillis = 260),
                initialOffsetY = enterOffset
            ) + fadeIn(animationSpec = tween(durationMillis = 180))) togetherWith
                (slideOutVertically(
                    animationSpec = tween(durationMillis = 220),
                    targetOffsetY = exitOffset
                ) + fadeOut(animationSpec = tween(durationMillis = 120)))
        },
        label = label
    ) { section ->
        content(section)
    }
}
