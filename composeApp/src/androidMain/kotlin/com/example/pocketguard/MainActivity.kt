package com.example.pocketguard


import android.os.Bundle
import android.os.Looper
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Android MainActivity
 * 
 * Entry point untuk Android app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashScreen = true
        Handler(Looper.getMainLooper()).postDelayed({
            keepSplashScreen = false
        }, 1500)

        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()


        setContent {
            App()
        }
    }
}
