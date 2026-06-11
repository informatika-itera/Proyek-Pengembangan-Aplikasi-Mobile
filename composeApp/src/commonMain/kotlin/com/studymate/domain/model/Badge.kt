package com.studymate.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Badge(
    val name: String,
    val icon: ImageVector,
    val isEarned: Boolean,
    val color: Color
)
