package com.kelazzz.app.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * QrCodeScannerView adalah expect Composable function untuk pemindai QR Code.
 * Komponen ini akan diimplementasikan secara native (actual) pada platform Android menggunakan CameraX dan ML Kit.
 *
 * @param onQrCodeScanned Callback yang dipanggil saat QR Code berhasil terdeteksi, mengembalikan teks token.
 * @param modifier Modifier untuk styling layout scanner.
 */
@Composable
expect fun QrCodeScannerView(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
)
