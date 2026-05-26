package com.example.fitgen.core.util

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberCameraLauncher(onPictureTaken: (ByteArray?) -> Unit): () -> Unit {
    // Membuka kamera bawaan HP tanpa perlu setting permission rumit
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Ubah Bitmap dari kamera menjadi ByteArray agar bisa dikirim ke Gemini
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            onPictureTaken(stream.toByteArray())
        } else {
            onPictureTaken(null) // Jika user batal foto
        }
    }

    return { launcher.launch(null) }
}