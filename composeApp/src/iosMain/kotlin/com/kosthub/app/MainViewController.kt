package com.kosthub.app

import androidx.compose.ui.window.ComposeUIViewController
import com.kosthub.app.platform.PlatformContext

fun MainViewController() = ComposeUIViewController {
    App(platformContext = PlatformContext(Unit))
}
