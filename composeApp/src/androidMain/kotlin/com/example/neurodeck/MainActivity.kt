package com.example.neurodeck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // CRITICAL: installSplashScreen() HARUS dipanggil SEBELUM super.onCreate().
        // Android docs: must be called before setting content view, otherwise
        // splash tidak transition smooth ke main activity.
        //
        // Mekanisme:
        //   1. System show splash dari Theme.NeuroDeck.Splash di AndroidManifest
        //      (background #F4F4F4 + icon ic_launcher_foreground)
        //   2. Setelah onCreate() selesai (Compose UI siap render), splash auto-dismiss
        //   3. Theme switch ke Theme.NeuroDeck (postSplashScreenTheme)
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}