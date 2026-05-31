package com.kelazzz.app.presentation.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Implementasi actual CameraPermissionGate untuk platform Android.
 * Menggunakan Activity Result API dan ContextCompat secara native.
 */
@Composable
actual fun CameraPermissionGate(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable (requestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current

    // Cek apakah izin kamera sudah diberikan sebelumnya
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher untuk meluncurkan dialog sistem meminta perizinan
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    // Minta izin kamera secara otomatis di awal saat komponen masuk ke dalam komposisi
    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        onPermissionGranted()
    } else {
        onPermissionDenied {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
}
