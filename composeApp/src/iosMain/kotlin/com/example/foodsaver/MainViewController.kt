package com.example.foodsaver

import androidx.compose.ui.window.ComposeUIViewController
import com.example.foodsaver.core.di.initKoinIOS

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Inisialisasi Koin untuk iOS
        initKoinIOS()
    }
) {
    App()
}
