package com.example.noteai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen sistem tapi jangan biarkan dia menahan layar
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Memastikan transisi ke konten aplikasi terjadi secepat mungkin
        enableEdgeToEdge()
        
        setContent {
            App()
        }
    }
}
