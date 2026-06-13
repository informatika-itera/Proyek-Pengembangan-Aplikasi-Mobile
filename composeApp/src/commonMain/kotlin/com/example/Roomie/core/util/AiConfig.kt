package com.example.Roomie.core.util

/**
 * Konfigurasi untuk Gemini AI Service.
 * File ini sekarang dilacak oleh Git agar build di CI/CD tidak fail.
 * JANGAN masukkan API Key asli di sini jika repository bersifat publik!
 */
object AiConfig {
    /**
     * Base URL untuk Google AI SDK / Gemini API
     */
    const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

    /**
     * Model yang digunakan (contoh: gemini-1.5-flash)
     */
    const val GEMINI_MODEL = "gemini-1.5-flash"

    /**
     * API Key dari Google AI Studio (https://aistudio.google.com/)
     * Gunakan dummy value untuk CI/CD, atau gunakan BuildConfig/Environment Variable untuk keamanan.
     */
    const val GEMINI_API_KEY = "YOUR_API_KEY_HERE"
}
