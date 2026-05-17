package com.example.tabungin.core.network

import platform.Foundation.NSBundle

actual object ApiConfig {
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
