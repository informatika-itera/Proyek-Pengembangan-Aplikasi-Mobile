package com.kelazzz.app.presentation.components

import androidx.compose.runtime.Composable

/**
 * CameraPermissionGate adalah sebuah Composable wrapper expect/actual untuk memproses perizinan kamera secara native.
 *
 * @param onPermissionGranted dipanggil jika izin akses kamera disetujui, menyajikan layar utama scan kamera.
 * @param onPermissionDenied dipanggil jika izin akses kamera ditolak, menyajikan tombol/UI untuk meminta ulang izin kamera.
 */
@Composable
expect fun CameraPermissionGate(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable (requestPermission: () -> Unit) -> Unit
)
