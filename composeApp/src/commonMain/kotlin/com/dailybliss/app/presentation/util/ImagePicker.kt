package com.dailybliss.app.presentation.util

import androidx.compose.runtime.Composable

import com.dailybliss.app.core.util.PlatformContext

@Composable
expect fun rememberImagePickerLauncher(
    onResult: (ByteArray?) -> Unit
): ImagePickerLauncher

interface ImagePickerLauncher {
    fun launch()
}

expect class FileStorage(context: PlatformContext) {
    suspend fun saveImage(bytes: ByteArray): String?
}
