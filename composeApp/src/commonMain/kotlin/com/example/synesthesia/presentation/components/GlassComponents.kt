package com.example.synesthesia.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun GlassCardPreview() {
    AuroraBackground {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GlassCard(
                modifier = Modifier.size(300.dp, 200.dp).padding(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.Text("Glass Card", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 16.dp,
    cornerRadius: Dp = 24.dp,
    borderOpacity: Float = 0.15f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = borderOpacity),
                        Color.White.copy(alpha = borderOpacity * 0.5f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .blur(blurRadius),
        content = content
    )
}

@Composable
fun GlowOrb(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp = 200.dp,
    blurRadius: Dp = 80.dp,
    durationMillis: Int = 5000
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateValue(
        initialValue = -20.dp,
        targetValue = 20.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .offset(y = animatedOffset)
            .size(size)
            .background(color, shape = CircleShape)
            .blur(blurRadius)
    )
}

@Composable
fun AuroraBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0D17))
    ) {
        // Glow Orbs
        GlowOrb(
            modifier = Modifier.align(Alignment.TopStart).offset(x = (-50).dp, y = 100.dp),
            color = Color(0xFF4A90E2).copy(alpha = 0.4f),
            size = 300.dp
        )
        GlowOrb(
            modifier = Modifier.align(Alignment.CenterEnd).offset(x = 50.dp, y = (-100).dp),
            color = Color(0xFF9013FE).copy(alpha = 0.3f),
            size = 350.dp
        )
        GlowOrb(
            modifier = Modifier.align(Alignment.BottomStart).offset(x = (-20).dp, y = 50.dp),
            color = Color(0xFF50E3C2).copy(alpha = 0.25f),
            size = 250.dp
        )

        content()
    }
}
