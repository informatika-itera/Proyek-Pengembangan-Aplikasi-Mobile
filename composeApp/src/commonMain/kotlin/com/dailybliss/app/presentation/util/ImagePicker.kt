package com.dailybliss.app.presentation.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(
    onResult: (ByteArray?) -> Unit
): ImagePickerLauncher

interface ImagePickerLauncher {
    fun launch()
}

expect object FileStorage {
    suspend fun saveImage(bytes: ByteArray): String?
}
