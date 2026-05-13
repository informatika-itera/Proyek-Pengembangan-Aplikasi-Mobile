package com.studyhub.core.network

import platform.Foundation.NSBundle

/**
 * iOS implementation of ApiConfig
 * 
 * Mengambil API key dari Info.plist atau environment.
 * 
 * Setup:
 * 1. Buka iosApp/iosApp/Info.plist
 * 2. Tambahkan key: GROQ_API_KEY dengan value: your_api_key
 * 
 * Atau untuk development, bisa hardcode langsung (JANGAN untuk production!)
 */
actual object ApiConfig {
    actual val groqApiKey: String
        get() {
            // Try to get from Info.plist
            val plistValue = NSBundle.mainBundle.objectForInfoDictionaryKey("GROQ_API_KEY") as? String
            
            return plistValue ?: run {
                // Fallback untuk development - GANTI dengan API key Anda
                // WARNING: Jangan commit API key ke repository!
                ""
            }
        }
}
