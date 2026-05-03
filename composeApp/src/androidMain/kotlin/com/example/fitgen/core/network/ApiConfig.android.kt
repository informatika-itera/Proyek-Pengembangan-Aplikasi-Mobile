package com.example.fitgen.core.network

import com.example.fitgen.BuildConfig

/**
 * ApiConfig (androidMain)
 * Mengambil nilai geminiApiKey yang disuntikkan lewat BuildConfig oleh Gradle
 */
actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
}
