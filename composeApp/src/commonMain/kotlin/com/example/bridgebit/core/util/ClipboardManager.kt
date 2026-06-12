package com.example.bridgebit.core.util

/**
 * KMP Clipboard Manager
 *
 * Menyediakan abstraksi lintas platform untuk menyalin teks ke clipboard sistem.
 * Implementasi platform-spesifik disediakan via `actual` di androidMain dan iosMain.
 */
expect fun copyToClipboard(text: String)
