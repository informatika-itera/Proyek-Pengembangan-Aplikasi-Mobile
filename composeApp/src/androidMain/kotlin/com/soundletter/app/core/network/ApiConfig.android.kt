package com.soundletter.app.core.network

import com.soundletter.app.BuildConfig

/**
 * Android implementation of ApiConfig
 */
actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
}
