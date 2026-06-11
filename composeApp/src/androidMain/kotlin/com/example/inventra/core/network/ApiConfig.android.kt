package com.example.inventra.core.network

import com.example.inventra.BuildConfig

actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
    actual val supabaseUrl: String = BuildConfig.SUPABASE_URL
    actual val supabaseAnonKey: String = BuildConfig.SUPABASE_ANON_KEY
    actual val supabaseServiceRoleKey: String = BuildConfig.SUPABASE_SERVICE_ROLE_KEY
}