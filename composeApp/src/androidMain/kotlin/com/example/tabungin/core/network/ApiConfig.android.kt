package com.example.tabungin.core.network

import com.example.tabungin.BuildConfig

actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
}
