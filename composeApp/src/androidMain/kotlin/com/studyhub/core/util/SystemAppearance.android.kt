package com.studyhub.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
actual fun SystemAppearance(isDarkMode: Boolean) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isDarkMode

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
}
