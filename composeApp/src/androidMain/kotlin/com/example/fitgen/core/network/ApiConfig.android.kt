package com.example.fitgen.core.network

import com.example.fitgen.BuildKonfig

/**
 * ApiConfig (androidMain)
 * Mengambil nilai geminiApiKey yang disuntikkan lewat BuildKonfig oleh Gradle
 */
actual object ApiConfig {
    actual val geminiApiKey: String = BuildKonfig.GEMINI_API_KEY
}