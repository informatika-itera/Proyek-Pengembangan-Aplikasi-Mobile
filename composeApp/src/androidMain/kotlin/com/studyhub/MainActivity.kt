package com.studyhub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Android MainActivity
 * 
 * Entry point untuk Android app.
 */
class MainActivity : ComponentActivity() {

    private var openScreen by mutableStateOf<String?>(null)
    private var taskId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            App(
                openScreen = openScreen,
                taskId = taskId,
                onScreenOpened = {
                    openScreen = null
                    taskId = null
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        openScreen = intent?.getStringExtra("openScreen")
        taskId = intent?.getStringExtra("taskId")
    }
}
