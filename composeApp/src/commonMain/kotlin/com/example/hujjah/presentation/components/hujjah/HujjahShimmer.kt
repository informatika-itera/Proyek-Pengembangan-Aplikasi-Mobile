package com.example.hujjah.presentation.components.hujjah

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hujjah.presentation.theme.LocalHujjahColors

@Composable
fun shimmerBrush(): Brush {
    val colors = LocalHujjahColors.current
    
    // iOS monochrome premium colors for shimmer
    val baseColor = if (colors.isDarkTheme) Color(0xFF1C1C1E) else Color(0xFFE5E5EA)
    val highlightColor = if (colors.isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}

@Composable
fun ShimmerSurahItem(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Star Octagram shape placeholder
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(brush, shape = RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
        }
        
        // Arabic text on the right placeholder
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(24.dp)
                .background(brush, shape = RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun ShimmerHadithItem(brush: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(brush, shape = RoundedCornerShape(20.dp))
    )
}
