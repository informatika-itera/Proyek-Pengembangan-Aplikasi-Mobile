package com.example.foodsaver.core.network

/**
 * Implementasi iOS untuk ApiConfig.
 * Untuk iOS, API Key bisa diambil dari Build Settings atau Environment Variables.
 */
actual object ApiConfig {
    actual val apiKey: String = "" // Akan dikonfigurasi via XCConfig di tahap berikutnya
}
