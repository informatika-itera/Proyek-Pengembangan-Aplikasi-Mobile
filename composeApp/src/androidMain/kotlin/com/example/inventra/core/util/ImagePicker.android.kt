package com.example.inventra.core.util

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

actual class ImagePicker(private val activity: ComponentActivity) {
    actual fun pickImage(onImagePicked: (ByteArray, String) -> Unit) {
        val launcher = activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val bytes = activity.contentResolver
                    .openInputStream(it)?.readBytes() ?: return@let
                val fileName = "item_${System.currentTimeMillis()}.jpg"
                onImagePicked(bytes, fileName)
            }
        }
        launcher.launch("image/*")
    }
}