package com.studyhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.studyhub.presentation.theme.Spacing

@Composable
fun glassSurfaceColor(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) Color.Black.copy(alpha = 0.25f) 
           else Color.White.copy(alpha = 0.8f)
}

@Composable
fun glassBorderColor(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.3f)
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderAlpha: Float = 0.3f,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    // Use white/black with opacity for glass effect, but keep it clean
    val baseColor = if (isDark) Color.Black else Color.White
    val bgColor = if (isDark) baseColor.copy(alpha = 0.15f) else baseColor.copy(alpha = 0.12f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else baseColor.copy(alpha = borderAlpha)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
    ) {
        // Rim Light Effect (Top highlight)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )
        
        Column(
            modifier = Modifier.padding(Spacing.normal),
            content = content
        )
    }
}

@Composable
fun GlassIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    hasNotification: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.20f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.30f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription, tint = Color.White, modifier = Modifier.size(22.dp))
            
            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .border(1.dp, MaterialTheme.colorScheme.onError, CircleShape)
                )
            }
        }
    }
}

@Composable
fun PillBadge(
    text: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.medium, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = text
        )
    }
}
