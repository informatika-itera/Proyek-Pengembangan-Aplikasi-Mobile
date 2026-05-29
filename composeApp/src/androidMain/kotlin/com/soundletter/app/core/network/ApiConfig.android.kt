package com.soundletter.app.core.network

import com.soundletter.app.BuildConfig

/**
 * Android implementation of ApiConfig using BuildConfig
 */
actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
    actual val spotifyClientId: String = BuildConfig.SPOTIFY_CLIENT_ID
    actual val spotifyClientSecret: String = BuildConfig.SPOTIFY_CLIENT_SECRET
    actual val supabaseUrl: String = BuildConfig.SUPABASE_URL
    actual val supabaseAnonKey: String = BuildConfig.SUPABASE_ANON_KEY
}
