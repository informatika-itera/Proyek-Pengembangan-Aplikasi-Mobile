package com.example.synesthesia.core.util

import android.content.Intent
import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AndroidSharer : KoinComponent {
    val context: Context by inject()

    fun share(title: String, content: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\n$content\n\n- Sent from Synesthesia")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }
}

actual fun shareNote(title: String, content: String) {
    AndroidSharer.share(title, content)
}
