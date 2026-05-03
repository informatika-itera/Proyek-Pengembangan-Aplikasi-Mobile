package com.example.fitgen.core.network

import platform.Foundation.NSBundle

/**
 * ApiConfig (iosMain)
 * Mengambil nilai geminiApiKey dari Info.plist pada iOS
 */
actual object ApiConfig {
    actual val geminiApiKey: String = NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String ?: ""
}
