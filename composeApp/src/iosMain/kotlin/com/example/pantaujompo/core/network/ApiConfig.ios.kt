package com.example.pantaujompo.core.network

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

    actual val geminiApiKeyNutrisi: String
        get() {
            // Ambil langsung dari Info.plist Xcode
            return NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY_NUTRISI") as? String ?: ""
        }

    actual val geminiApiKeyChat: String
        get() {
            // Ambil langsung dari Info.plist Xcode
            return NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY_CHAT") as? String ?: ""
        }
}
