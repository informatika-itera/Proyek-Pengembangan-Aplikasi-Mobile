package com.example.pantaujompo.core.network

import com.example.pantaujompo.BuildConfig

actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
}