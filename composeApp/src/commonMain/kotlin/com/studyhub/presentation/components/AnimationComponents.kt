package com.studyhub.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

val LocalReduceMotion = compositionLocalOf { false }

@Composable
fun StaggeredItem(
    index: Int,
    content: @Composable () -> Unit
) {
    val reduceMotion = LocalReduceMotion.current
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!reduceMotion) {
            kotlinx.coroutines.delay(index * 50L)
        }
        visible = true
    }

    if (reduceMotion) {
        content()
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { 20 }
        ) {
            content()
        }
    }
}
