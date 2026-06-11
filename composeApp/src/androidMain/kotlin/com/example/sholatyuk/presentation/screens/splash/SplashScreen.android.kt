package com.example.sholatyuk.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sholatyuk.R
import com.example.sholatyuk.presentation.theme.DeepBlue
import com.example.sholatyuk.presentation.theme.DarkTeal
import kotlinx.coroutines.delay

@Composable
actual fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(2000L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkTeal, DeepBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_sholatyuk),
            contentDescription = "Logo",
            modifier = Modifier
                .size(250.dp)
                .scale(scale.value)
        )
    }
}

private class OvershootInterpolator(private val tension: Float = 2f) {
    fun getInterpolation(t: Float): Float {
        var time = t
        time -= 1.0f
        return time * time * ((tension + 1) * time + tension) + 1.0f
    }
}
