package com.studyhub.core.network

import com.studyhub.BuildConfig

/**
 * Android implementation of ApiConfig
 * 
 * Mengambil API key dari BuildConfig yang di-generate
 * dari local.properties saat build time.
 * 
 * Setup:
 * 1. Buat file local.properties di root project
 * 2. Tambahkan: GROQ_API_KEY=your_api_key_here
 * 3. Build project (API key akan di-inject ke BuildConfig)
 */
actual object ApiConfig {
    actual val groqApiKey: String = BuildConfig.GROQ_API_KEY
}
