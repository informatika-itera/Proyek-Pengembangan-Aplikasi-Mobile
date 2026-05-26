package com.example.hujjah.core.network

import platform.Foundation.NSBundle

/**
 * iOS implementation of ApiConfig
 * 
 * Mengambil API key dari Info.plist atau environment.
 * 
 * Setup:
 * 1. Buka iosApp/iosApp/Info.plist
 * 2. Tambahkan key: GEMINI_API_KEY dengan value: your_api_key
 * 
 * Atau untuk development, bisa hardcode langsung (JANGAN untuk production!)
 */
actual object ApiConfig {
    actual val QURAN_BASE_URL: String = "https://quran-api-id.vercel.app"
    actual val HADITH_BASE_URL: String = "https://api.hadith.gading.dev"
    actual val GEMINI_BASE_URL: String = "https://generativelanguage.googleapis.com/v1beta/models/"

    actual val geminiApiKey: String
        get() {
            // Try to get from Info.plist
            val plistValue = NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String
            
            return plistValue ?: run {
                // Fallback untuk development - GANTI dengan API key Anda
                // WARNING: Jangan commit API key ke repository!
                ""
            }
        }
}
