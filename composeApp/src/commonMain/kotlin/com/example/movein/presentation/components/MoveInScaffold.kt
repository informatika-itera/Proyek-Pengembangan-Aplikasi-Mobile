package com.example.movein.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.movein.presentation.theme.MoveInTheme

@Composable
fun MoveInScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    containerColor: Color = MoveInTheme.colors.backgroundPrimary,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor)
    ) {
        // Subtle background gradient/accent
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MoveInTheme.colors.backgroundSecondary,
                            MoveInTheme.colors.backgroundPrimary
                        )
                    )
                )
        )

        Scaffold(
            topBar = topBar,
            bottomBar = bottomBar,
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}
