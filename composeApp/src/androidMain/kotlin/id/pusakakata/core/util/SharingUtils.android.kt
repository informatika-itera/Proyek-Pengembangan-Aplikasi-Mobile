package id.pusakakata.core.util

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberShareManager(): ShareManager {
    val context = LocalContext.current
    return remember(context) {
        AndroidShareManager(context)
    }
}

class AndroidShareManager(private val context: android.content.Context) : ShareManager {
    override fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Kosakata"))
    }
}
