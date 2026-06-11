package com.example.foodsaver

import androidx.compose.ui.window.ComposeUIViewController
import com.example.foodsaver.core.injection.initKoinIOS

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoinIOS()
    }
) {
    App()
}
