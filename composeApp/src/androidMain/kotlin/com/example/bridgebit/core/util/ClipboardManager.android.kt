package com.example.bridgebit.core.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.bridgebit.NoteAIApplication

/**
 * Implementasi Android untuk copyToClipboard.
 *
 * Menggunakan [ClipboardManager] sistem Android untuk menyalin teks
 * ke clipboard, sehingga pengguna dapat paste di aplikasi manapun.
 */
actual fun copyToClipboard(text: String) {
    val context: Context = NoteAIApplication.instance
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("BridgeBit Translation", text)
    clipboardManager.setPrimaryClip(clip)
}
