package com.example.bridgebit.core.util

import platform.UIKit.UIPasteboard

/**
 * Implementasi iOS untuk copyToClipboard.
 *
 * Menggunakan [UIPasteboard.general] untuk menyalin teks ke system pasteboard iOS,
 * sehingga pengguna dapat paste di aplikasi manapun di perangkat iOS mereka.
 */
actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}
