package com.example.neurodeck.core.di

/**
 * iOS implementation: placeholder.
 *
 * Tim NeuroDeck fokus Android di Sprint 2-3. iOS deployment belum di-test,
 * jadi return string kosong yang akan trigger error di GeminiService
 * (intentional: kalau ada developer iterate iOS, langsung tahu harus implement).
 */
actual fun getApiKey(): String {
    // TODO Sprint 5: load dari iOS plist atau env var
    return ""
}