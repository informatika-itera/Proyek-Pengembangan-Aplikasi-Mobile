package com.example.tabungin.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.tabungin.R

@Composable
actual fun painterResourceLogo(): Painter {
    return painterResource(R.drawable.tabungin_logo)
}