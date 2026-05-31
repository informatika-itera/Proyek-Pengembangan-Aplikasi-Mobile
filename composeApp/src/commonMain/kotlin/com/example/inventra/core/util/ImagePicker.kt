package com.example.inventra.core.util

// expect class untuk akses galeri foto per platform
expect class ImagePicker {
    // Callback dipanggil dengan bytes foto yang dipilih dan nama file
    fun pickImage(onImagePicked: (ByteArray, String) -> Unit)
}