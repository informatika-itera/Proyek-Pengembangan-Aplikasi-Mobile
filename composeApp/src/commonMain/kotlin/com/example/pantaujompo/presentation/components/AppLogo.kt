package com.example.pantaujompo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 100.dp,
    iconSize: Dp = 50.dp,
    textSize: TextUnit = 30.sp,
    showSubtitle: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Keren: Lingkaran Gradient dengan ikon orang lari
        Box(
            modifier = Modifier
                .size(logoSize)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF00FFCC), Color(0xFF00B3FF))))
                .padding((logoSize.value * 0.03f).dp) // Responsive padding
                .clip(CircleShape)
                .background(Color(0xFF121212)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = "Logo PantauJompo",
                tint = Color(0xFF00FFCC),
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.height((logoSize.value * 0.24f).dp))
        Text(
            text = "Pantau Jompo",
            color = Color.White,
            fontSize = textSize,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        if (showSubtitle) {
            Text(
                text = "Nutrisi & Olahraga Sehat",
                color = Color(0xFF00FFCC),
                fontSize = (textSize.value * 0.46f).sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}
