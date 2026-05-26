package com.example.hujjah.core.network

import com.example.hujjah.BuildConfig

/**
 * Android implementation of ApiConfig
 * 
 * Mengambil API key dari BuildConfig yang di-generate
 * dari local.properties saat build time.
 * 
 * Setup:
 * 1. Buat file local.properties di root project
 * 2. Tambahkan: GEMINI_API_KEY=your_api_key_here
 * 3. Build project (API key akan di-inject ke BuildConfig)
 */
actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
    actual val QURAN_BASE_URL: String = "https://quran-api-id.vercel.app"
    actual val HADITH_BASE_URL: String = "https://api.hadith.gading.dev"
    actual val GEMINI_BASE_URL: String = "https://generativelanguage.googleapis.com/v1beta/models/"
}
