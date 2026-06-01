package com.mywallet.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun AppLogoIcon(
    modifier: Modifier = Modifier,
    useOuterRing: Boolean = false
) {
    val darkBlue = Color(0xFF001F3F)
    val white = Color.White

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val centerY = h / 2
        val radius = size.minDimension / 2
        
        // 1. Draw Outer Ring (if requested)
        // Now using darkBlue instead of lightBlue
        if (useOuterRing) {
            drawCircle(
                color = darkBlue,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = radius * 0.15f)
            )
        }

        // 2. Draw Dark Blue Circle Background
        drawCircle(
            color = darkBlue,
            radius = if (useOuterRing) radius * 0.8f else radius,
            center = Offset(centerX, centerY)
        )

        // 3. Draw White Wallet Icon
        val walletScale = if (useOuterRing) 0.5f else 0.6f
        val iconWidth = w * walletScale
        val iconHeight = iconWidth * 0.7f
        
        val iconX = centerX - (iconWidth / 2)
        val iconY = centerY - (iconHeight / 2)

        // Main Wallet Body
        drawRoundRect(
            color = white,
            topLeft = Offset(iconX, iconY),
            size = Size(iconWidth * 0.85f, iconHeight),
            cornerRadius = CornerRadius(iconHeight * 0.25f)
        )

        // The Flap
        val flapWidth = iconWidth * 0.38f
        val flapHeight = iconHeight * 0.48f
        val flapX = iconX + iconWidth * 0.62f
        val flapY = centerY - (flapHeight / 2)

        drawRoundRect(
            color = white,
            topLeft = Offset(flapX, flapY),
            size = Size(flapWidth, flapHeight),
            cornerRadius = CornerRadius(flapHeight * 0.35f)
        )

        // The Snap (Inner dark dot)
        drawCircle(
            color = darkBlue,
            radius = flapHeight * 0.14f,
            center = Offset(flapX + (flapWidth * 0.68f), centerY)
        )
        
        // Very tiny white dot in center of snap
        drawCircle(
            color = white,
            radius = flapHeight * 0.04f,
            center = Offset(flapX + (flapWidth * 0.68f), centerY)
        )
    }
}
