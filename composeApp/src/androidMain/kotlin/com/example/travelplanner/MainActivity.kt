package com.example.travelplanner

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.travelplanner.core.util.VoiceInputManager

/**
 * Android MainActivity
 * 
 * Entry point untuk Android app, lengkap dengan integrasi Speech Recognition asli.
 */
class MainActivity : ComponentActivity() {

    // Register ActivityResultLauncher for Speech-to-Text overlay intent
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                VoiceInputManager.onVoiceInputResult?.invoke(spokenText)
            } else {
                VoiceInputManager.onVoiceInputResult?.invoke("Error: Teks tidak terdeteksi")
            }
        } else {
            VoiceInputManager.onVoiceInputResult?.invoke("Error: Perekaman suara dibatalkan")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Wire shared VoiceInputManager to Android native implementation
        VoiceInputManager.voiceInputLauncher = {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID") // Default to Indonesian
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan bicara untuk mencatat pengeluaran...")
                }
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                // Speech recognition not supported on device (e.g., emulator without Speech Services)
                VoiceInputManager.onVoiceInputResult?.invoke("Error: Layanan suara tidak tersedia (${e.message})")
            }
        }
        
        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent memory leaks by clearing launcher callbacks when Activity is destroyed
        if (VoiceInputManager.voiceInputLauncher != null) {
            VoiceInputManager.voiceInputLauncher = null
        }
    }
}
