package com.example.fitgen.core.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCameraLauncher(onPictureTaken: (ByteArray?) -> Unit): () -> Unit {
    // Untuk saat ini dikosongkan dulu agar fokus ke Android
    return { onPictureTaken(null) }
}