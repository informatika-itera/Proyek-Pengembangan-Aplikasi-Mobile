package com.example.neurodeck

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    // Launcher untuk minta izin POST_NOTIFICATIONS (Android 13+).
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* granted atau tidak — tidak perlu aksi khusus */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        // CRITICAL: installSplashScreen() HARUS dipanggil SEBELUM super.onCreate().
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Minta izin notifikasi di Android 13+ kalau belum di-grant.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            App()
        }
    }
}