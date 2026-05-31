package com.example.inventra.core.network

import platform.Foundation.NSBundle

actual object ApiConfig {
    actual val geminiApiKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String ?: ""

    actual val supabaseUrl: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_URL") as? String ?: ""

    actual val supabaseAnonKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_ANON_KEY") as? String ?: ""
}