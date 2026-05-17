package com.dailybliss.app.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import com.dailybliss.app.core.util.PlatformContext

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

actual class FileStorage actual constructor(
    context: PlatformContext
) {
    actual suspend fun saveImage(bytes: ByteArray): String? = null
}
