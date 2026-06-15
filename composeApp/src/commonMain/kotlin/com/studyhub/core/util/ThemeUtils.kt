package com.studyhub.core.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun headerBrownColor(): Color = if (isSystemInDarkTheme())
    Color(0xFF5C4A28) else Color(0xFF8B7355)
