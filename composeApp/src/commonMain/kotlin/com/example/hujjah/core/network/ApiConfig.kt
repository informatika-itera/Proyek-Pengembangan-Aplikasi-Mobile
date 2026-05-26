package com.example.hujjah.core.network

/**
 * API Configuration - expect declaration
 * 
 * Ini adalah contoh penggunaan expect/actual pattern untuk
 * kode platform-specific. API key disimpan berbeda di Android dan iOS.
 * 
 * expect: Deklarasi tanpa implementasi (di commonMain)
 * actual: Implementasi spesifik platform (di androidMain/iosMain)
 */
expect object ApiConfig {
    /**
     * Gemini API Key
     * 
     * Android: Diambil dari BuildConfig (local.properties)
     * iOS: Diambil dari Info.plist atau hardcoded (untuk development)
     */
    val geminiApiKey: String
    val QURAN_BASE_URL: String
    val HADITH_BASE_URL: String
    val GEMINI_BASE_URL: String
}
