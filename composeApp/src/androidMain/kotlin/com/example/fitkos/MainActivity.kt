package com.example.fitkos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Android MainActivity
 *
 * Entry point untuk Android app.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        val skipSplash = intent.getBooleanExtra(
            EXTRA_SKIP_SPLASH_FOR_UI_TEST,
            false
        )

        setContent {
            App(
                skipSplash = skipSplash
            )
        }
    }

    companion object {
        const val EXTRA_SKIP_SPLASH_FOR_UI_TEST = "extra_skip_splash_for_ui_test"
    }
}