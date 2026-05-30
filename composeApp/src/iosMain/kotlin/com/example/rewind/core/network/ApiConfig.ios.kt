package com.example.rewind.core.network

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
    actual val geminiApiKey: String
        get() {
            // Try to get from Info.plist
            val plistValue = NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String
            actual val tmdbApiKey: String = "6396df0de70076546312556c789a235d"

            return plistValue ?: run {
                // Fallback untuk development - GANTI dengan API key Anda
                // WARNING: Jangan commit API key ke repository!
                ""
            }
        }
}
