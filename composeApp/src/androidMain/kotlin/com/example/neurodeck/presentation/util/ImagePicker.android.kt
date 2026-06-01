package com.example.neurodeck.presentation.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// ════════════════════════════════════════════════════════════════════════════
// ImagePicker.android.kt — ACTUAL implementation untuk Android
//
// Pakai ActivityResultContracts.PickVisualMedia() = Android Photo Picker modern:
//   - TIDAK butuh permission (system picker, jalan di proses terpisah)
//   - Tersedia Android 4.4+ (backport via Play Services)
//   - User cuma bisa pilih foto yang dia mau (privacy-friendly)
//
// Setelah pick: copy bytes ke internal storage app supaya path permanen.
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri == null) {
            // User batal pilih
            onResult(null)
        } else {
            // Copy ke internal storage di background thread
            scope.launch {
                val path = withContext(Dispatchers.IO) {
                    copyImageToInternalStorage(context, uri)
                }
                onResult(path)
            }
        }
    }

    return remember {
        ImagePickerLauncher {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        }
    }
}

/**
 * Copy image dari content:// URI ke internal storage app.
 *
 * @return file:// path string yang bisa di-load Coil, atau null kalau gagal.
 *
 * Strategi:
 *   1. Hapus avatar lama (prefix "avatar_") supaya tidak menumpuk
 *   2. Buat file baru dengan timestamp (cache-bust Coil)
 *   3. Copy bytes via contentResolver
 *   4. Return file:// + absolutePath
 */
private fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        // Bersihkan avatar lama
        context.filesDir.listFiles { f -> f.name.startsWith("avatar_") }
            ?.forEach { it.delete() }

        val file = File(context.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return null  // openInputStream null → gagal

        // file:// scheme supaya Coil pasti bisa resolve
        "file://${file.absolutePath}"
    } catch (e: Exception) {
        null
    }
}