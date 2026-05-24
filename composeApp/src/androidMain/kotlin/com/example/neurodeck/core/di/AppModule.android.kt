package com.example.neurodeck.core.di

import com.example.neurodeck.BuildConfig

/**
 * Android implementation: ambil API key dari BuildConfig yang di-inject saat build.
 *
 * BuildConfig.GEMINI_API_KEY berasal dari `local.properties` di root project,
 * di-inject via build.gradle.kts buildConfigField.
 *
 * Kalau key kosong (developer lupa setup local.properties), throw exception
 * supaya app crash dengan pesan jelas (lebih baik daripada silent fail saat user
 * coba generate flashcard dan dapat error "API key tidak valid").
 */
actual fun getApiKey(): String {
    val key = BuildConfig.GEMINI_API_KEY
    check(key.isNotBlank() && key != "dummy_for_now") {
        "GEMINI_API_KEY tidak di-set di local.properties. " +
                "Lihat README untuk setup instructions."
    }
    return key
}

