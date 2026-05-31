package com.example.inventra.core.network

expect object ApiConfig {
    val geminiApiKey: String
    val supabaseUrl: String
    val supabaseAnonKey: String
}