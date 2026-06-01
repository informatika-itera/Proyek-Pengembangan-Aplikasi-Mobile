package com.mywallet.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MyWalletLogo: ImageVector
    get() = ImageVector.Builder(
        name = "MyWalletLogo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(4f, 4f)
            verticalLineTo(20f)
            horizontalLineTo(20f)
            verticalLineTo(16f)
            horizontalLineTo(18f)
            verticalLineTo(18f)
            horizontalLineTo(6f)
            verticalLineTo(6f)
            horizontalLineTo(18f)
            verticalLineTo(8f)
            horizontalLineTo(20f)
            verticalLineTo(4f)
            close()
        }
        path(fill = SolidColor(Color.White)) {
            moveTo(14f, 10f)
            horizontalLineTo(20f)
            verticalLineTo(14f)
            horizontalLineTo(14f)
            close()
        }
    }.build()
