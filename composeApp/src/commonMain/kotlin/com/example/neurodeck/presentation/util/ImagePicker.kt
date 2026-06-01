package com.example.neurodeck.presentation.util

import androidx.compose.runtime.Composable

// ════════════════════════════════════════════════════════════════════════════
// ImagePicker — expect/actual untuk pilih foto dari galeri (Sprint 3)
//
// Pattern KMP: expect declaration di commonMain, actual di androidMain (real
// Android Photo Picker) dan iosMain (stub — project ini Android-only).
//
// Desain:
//   - rememberImagePickerLauncher() = @Composable factory, return launcher
//   - launcher.launch() = buka galeri sistem
//   - onResult(path) = callback dengan PATH file lokal (sudah di-copy ke
//     internal storage app), atau null kalau user cancel / error
//
// Kenapa copy ke internal storage (bukan simpan content:// URI):
//   - content:// URI dari picker bisa EXPIRE setelah app restart
//   - File internal storage app PERMANEN sampai app uninstall
//   - Tidak butuh permission khusus untuk baca file milik app sendiri
//   - Coil bisa load file:// path tanpa masalah
// ════════════════════════════════════════════════════════════════════════════

/**
 * Launcher untuk image picker. Dibuat via [rememberImagePickerLauncher].
 * Panggil [launch] untuk membuka galeri.
 */
expect class ImagePickerLauncher {
    fun launch()
}

/**
 * Composable factory untuk image picker launcher.
 *
 * @param onResult Callback dengan path file lokal hasil copy (file:// scheme),
 *                 atau null kalau user batal / terjadi error.
 */
@Composable
expect fun rememberImagePickerLauncher(
    onResult: (String?) -> Unit,
): ImagePickerLauncher