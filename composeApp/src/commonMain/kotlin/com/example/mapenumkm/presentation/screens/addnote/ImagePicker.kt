package com.example.mapenumkm.presentation.screens.addnote

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onResult: (String?) -> Unit): () -> Unit
