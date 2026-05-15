package com.dailybliss.app.core.network

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
            
            return plistValue ?: ""
        }

    actual val geminiModelName: String
        get() {
            val plistValue = NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_MODEL_NAME") as? String
            return plistValue ?: "gemini-1.5-flash"
        }
}

