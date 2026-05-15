package com.dailybliss.app.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePickerLauncher(
    onResult: (ByteArray?) -> Unit
): ImagePickerLauncher {
    return remember {
        object : ImagePickerLauncher {
            override fun launch() {}
        }
    }
}

actual object FileStorage {
    actual suspend fun saveImage(bytes: ByteArray): String? = null
}
