package com.example.raillog.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberMediaPicker(
    onMediaPicked: (fileName: String, base64Data: String?) -> Unit
): MediaPicker {
    val context = LocalContext.current

    // Kamera tetap pakai TakePicturePreview dulu
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val base64String = Base64.encodeToString(
                outputStream.toByteArray(), Base64.NO_WRAP
            )
            onMediaPicked("Camera_${System.currentTimeMillis()}.jpg", base64String)
        } else {
            onMediaPicked("Error_Camera.jpg", null)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val outputStream = ByteArrayOutputStream()
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap == null) {
                    onMediaPicked("Error_File.jpg", null)
                    return@rememberLauncherForActivityResult
                }

                val maxSize = 1024
                val ratio = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
                val scaledBitmap = if (ratio < 1.0f) {
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * ratio).toInt(),
                        (bitmap.height * ratio).toInt(),
                        true
                    )
                } else {
                    bitmap
                }

                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val base64String = Base64.encodeToString(
                    outputStream.toByteArray(), Base64.NO_WRAP
                )
                onMediaPicked("Gallery_${System.currentTimeMillis()}.jpg", base64String)
            } catch (e: Exception) {
                e.printStackTrace()
                onMediaPicked("Error_File.jpg", null)
            }
        }
    }

    return remember {
        object : MediaPicker {
            override fun launchCamera() {
                cameraLauncher.launch(null)
            }
            override fun launchGallery() {
                galleryLauncher.launch("image/*")
            }
        }
    }
}
