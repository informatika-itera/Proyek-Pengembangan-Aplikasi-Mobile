package com.studymate.core.network

import platform.Foundation.NSBundle

actual object ApiConfig {
    actual val groqApiKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("GROQ_API_KEY") as? String ?: ""
        
    actual val googleWebClientId: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("GOOGLE_WEB_CLIENT_ID") as? String ?: ""
}
