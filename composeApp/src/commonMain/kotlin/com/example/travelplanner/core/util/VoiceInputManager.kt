package com.example.travelplanner.core.util

/**
 * VoiceInputManager
 * 
 * Shared manager to bridge Kotlin Multiplatform UI and platform-specific Speech-to-Text APIs.
 */
object VoiceInputManager {
    // Callback registered by MainActivity.kt on Android
    var voiceInputLauncher: (() -> Unit)? = null
    
    // Callback to pass the speech result back to the common UI
    var onVoiceInputResult: ((String) -> Unit)? = null

    /**
     * Triggers the platform-specific voice recognizer.
     * Fallback to simulator is handled in common code if launcher fails or returns an error.
     */
    fun startVoiceInput(onResult: (String) -> Unit) {
        onVoiceInputResult = onResult
        val launcher = voiceInputLauncher
        if (launcher != null) {
            launcher.invoke()
        } else {
            // No platform speech recognition registered, invoke callback with error to trigger fallback simulator
            onResult("Error: Speech recognition not supported on this platform")
        }
    }
}
