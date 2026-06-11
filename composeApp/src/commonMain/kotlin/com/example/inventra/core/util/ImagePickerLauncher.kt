package com.example.inventra.core.util

import androidx.compose.runtime.Composable

/**
 * Interface untuk image picker yang compatible dengan Compose.
 * Implementasi per platform via expect/actual.
 */
interface ImagePickerLauncher {
    fun pickImage()
    fun takePhoto()
}

/**
 * Composable expect function yang mengembalikan ImagePickerLauncher.
 * Dipanggil di level Composable, BUKAN di dalam onClick langsung.
 */
@Composable
expect fun rememberImagePickerLauncher(
    onImagePicked: (bytes: ByteArray, fileName: String) -> Unit
): ImagePickerLauncher