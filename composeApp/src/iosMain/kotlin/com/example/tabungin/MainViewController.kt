package com.example.tabungin

import androidx.compose.ui.window.ComposeUIViewController
import com.example.tabungin.core.di.initKoinIOS

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Initialize Koin for iOS
        initKoinIOS()
    }
) {
    App()
}
