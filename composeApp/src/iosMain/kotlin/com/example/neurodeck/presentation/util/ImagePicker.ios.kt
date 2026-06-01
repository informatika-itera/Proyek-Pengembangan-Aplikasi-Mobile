package com.example.neurodeck.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// ════════════════════════════════════════════════════════════════════════════
// ImagePicker.ios.kt — ACTUAL stub untuk iOS
//
// Project NeuroDeck saat ini Android-only (target utama tugas PAM).
// iOS actual disediakan HANYA supaya kompilasi metadata KMP hijau —
// expect declaration di commonMain wajib punya actual di SEMUA target
// yang dideklarasikan (termasuk iosX64/iosArm64/iosSimulatorArm64).
//
// Kalau project mau di-extend ke iOS nanti, implement di sini pakai
// UIImagePickerController atau PHPickerViewController.
// ════════════════════════════════════════════════════════════════════════════

actual class ImagePickerLauncher(
    private val onLaunch: () -> Unit,
) {
    actual fun launch() = onLaunch()
}

@Composable
actual fun rememberImagePickerLauncher(
    onResult: (String?) -> Unit,
): ImagePickerLauncher {
    // Stub: belum implement picker iOS. Return null supaya tidak crash.
    return remember { ImagePickerLauncher { onResult(null) } }
}