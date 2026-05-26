package com.example.hujjah.core.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onImagePicked: (ByteArray?) -> Unit): () -> Unit
