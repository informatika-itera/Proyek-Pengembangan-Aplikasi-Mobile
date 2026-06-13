package com.studymate.core.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onImagePicked: (String) -> Unit): () -> Unit
