package com.example.gamenews.core.network

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
    val geminiApiKey: String
    val gameBrainApiKey: String

}
