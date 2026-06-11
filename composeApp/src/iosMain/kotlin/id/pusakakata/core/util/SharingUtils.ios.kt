package id.pusakakata.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberShareManager(): ShareManager {
    return remember { IosShareManager() }
}

class IosShareManager : ShareManager {
    override fun shareText(text: String) {
        // Implement share on iOS if needed, otherwise ignore or log
    }
}
