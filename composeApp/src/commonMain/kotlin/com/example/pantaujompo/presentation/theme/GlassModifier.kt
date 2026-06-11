package com.example.pantaujompo.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Adaptive Glassmorphism modifier - works for both Dark and Light mode.
 */
fun Modifier.glassCard(
    shape: Shape = RoundedCornerShape(24.dp),
    borderWidth: Dp = 1.dp,
    neonColor: Color = NeonGreen
) = composed {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.09f), Color.White.copy(alpha = 0.02f))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color.White, Color.White) // Solid white for visibility
        )
    }

    val borderColor = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.2f),
                neonColor.copy(alpha = 0.4f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(Color(0xFFD1D5DB), Color(0xFFE5E7EB)) // Solid visible border
        )
    }

    if (isDark) {
        this
            .clip(shape)
            .background(brush = bgGradient)
            .border(width = borderWidth, brush = borderColor, shape = shape)
    } else {
        this
            .shadow(elevation = 6.dp, shape = shape, spotColor = Color.Black.copy(0.1f), ambientColor = Color.Black.copy(0.05f))
            .clip(shape)
            .background(Color.White)
    }
}

/**
 * Full-screen background with ambient glow blobs.
 * Dark mode: neon green + cyan blobs on near-black.
 * Light mode: subtle teal blobs on frosty white-grey.
 */
@Composable
fun MeshBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val bgColor = if (isDark) DarkBackground else LightBackground

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(bgColor)
                .drawBehind {
                    if (isDark) {
                        // Blob 1: NeonGreen top-left
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonGreen.copy(alpha = 0.12f), Color.Transparent),
                                center = Offset(0f, 0f),
                                radius = size.width * 0.75f
                            ),
                            radius = size.width * 0.75f,
                            center = Offset(0f, 0f)
                        )
                        // Blob 2: NeonCyan bottom-right
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonCyan.copy(alpha = 0.10f), Color.Transparent),
                                center = Offset(size.width, size.height),
                                radius = size.width * 0.85f
                            ),
                            radius = size.width * 0.85f,
                            center = Offset(size.width, size.height)
                        )
                        // Blob 3: Small purple mid-right
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF7B1FA2).copy(alpha = 0.06f), Color.Transparent),
                                center = Offset(size.width, size.height * 0.4f),
                                radius = size.width * 0.4f
                            ),
                            radius = size.width * 0.4f,
                            center = Offset(size.width, size.height * 0.4f)
                        )
                    } else {
                        // Light mode: No blobs, just a clean solid background for maximum contrast
                    }
                }
        )
        content()
    }
}
