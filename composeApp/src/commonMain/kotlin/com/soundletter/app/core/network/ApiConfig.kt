package com.soundletter.app.core.network

/**
 * Expected configuration for API keys across platforms
 */
expect object ApiConfig {
    val geminiApiKey: String
    val spotifyClientId: String
    val spotifyClientSecret: String
    val supabaseUrl: String
    val supabaseAnonKey: String
}
