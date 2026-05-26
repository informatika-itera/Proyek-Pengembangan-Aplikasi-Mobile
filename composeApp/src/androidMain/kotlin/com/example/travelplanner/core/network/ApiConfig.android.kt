package com.example.travelplanner.core.network

import com.example.travelplanner.BuildConfig

actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
}
