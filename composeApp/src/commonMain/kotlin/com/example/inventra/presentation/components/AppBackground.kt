package com.example.inventra.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import inventra.composeapp.generated.resources.Res
import inventra.composeapp.generated.resources.logo_hmif
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppBackground(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A1C1E),
                Color(0xFF111315)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFDFBFF),
                Color(0xFFF0F4F8)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        // Decorative blobs (optional for "cantik")
        // We can add them later if needed
        
        // Logo watermark
        Image(
            painter = painterResource(Res.drawable.logo_hmif),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(320.dp)
                .alpha(if (isDark) 0.15f else 0.20f),
            contentScale = ContentScale.Fit
        )
        
        Surface(
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}
