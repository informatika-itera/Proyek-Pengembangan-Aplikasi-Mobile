package id.pusakakata.core.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberShareManager(): ShareManager

interface ShareManager {
    fun shareText(text: String)
}
