package com.itera.news.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neumorphicShadow(
    offset: Dp = 8.dp,
    blur: Dp = 16.dp,
    lightShadowColor: Color = NeumorphicLightShadow,
    darkShadowColor: Color = NeumorphicDarkShadow,
    cornerRadius: Dp = 12.dp
): Modifier = this.drawBehind {
    val shadowPaint = Paint().apply {
        style = PaintingStyle.Fill
    }
    
    val frameworkPaint = shadowPaint.asFrameworkPaint()
    
    drawIntoCanvas { canvas ->
        // Dark Shadow (Bottom Right)
        frameworkPaint.color = darkShadowColor.toArgb()
        frameworkPaint.setShadowLayer(
            blur.toPx(),
            offset.toPx(),
            offset.toPx(),
            darkShadowColor.toArgb()
        )
        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            cornerRadius.toPx(),
            cornerRadius.toPx(),
            shadowPaint
        )

        // Light Shadow (Top Left)
        frameworkPaint.color = lightShadowColor.toArgb()
        frameworkPaint.setShadowLayer(
            blur.toPx(),
            -offset.toPx(),
            -offset.toPx(),
            lightShadowColor.toArgb()
        )
        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            cornerRadius.toPx(),
            cornerRadius.toPx(),
            shadowPaint
        )
    }
}
