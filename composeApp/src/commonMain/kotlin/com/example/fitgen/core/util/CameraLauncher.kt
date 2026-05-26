package com.example.fitgen.core.util

import androidx.compose.runtime.Composable

// Fungsi ini akan memanggil kamera dan mengembalikan ByteArray foto
@Composable
expect fun rememberCameraLauncher(onPictureTaken: (ByteArray?) -> Unit): () -> Unit