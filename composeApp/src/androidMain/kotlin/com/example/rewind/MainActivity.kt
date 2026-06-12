package com.example.rewind

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Android MainActivity
 * 
 * Entry point untuk Android app.
 */
class MainActivity : ComponentActivity() {

    /**
     * Launcher untuk request permission notifikasi (Android 13+ / API 33).
     * Tanpa permission ini, notifikasi akan di-block secara diam-diam.
     */
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission granted atau denied — app tetap lanjut jalan.
        // User bisa enable/disable kapan saja dari Settings HP.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Request notification permission untuk Android 13+ (API 33)
        requestNotificationPermission()
        
        setContent {
            App()
        }
    }

    /**
     * Minta izin POST_NOTIFICATIONS jika belum granted.
     * Hanya berlaku untuk Android 13+ (API 33 ke atas).
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }
}
