package com.studymate.core.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (String) -> Unit): () -> Unit {
    return {
        // iOS implementation would use PHPickerViewController or UIImagePickerController
        // For now, this is a placeholder to satisfy the compiler
    }
}
