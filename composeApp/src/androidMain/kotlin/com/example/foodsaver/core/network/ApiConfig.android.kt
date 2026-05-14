package com.example.foodsaver.core.network

import com.example.foodsaver.BuildConfig

/**
 * Implementasi Android untuk ApiConfig.
 * Mengambil API Key dari BuildConfig yang di-generate oleh Gradle.
 */
actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
}
