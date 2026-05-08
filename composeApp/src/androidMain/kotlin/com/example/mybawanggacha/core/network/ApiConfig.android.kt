package com.example.mybawanggacha.core.network

actual object ApiConfig {
    private var _geminiApiKey: String = ""

    fun initialize(apiKey: String) {
        _geminiApiKey = apiKey
    }

    actual val geminiApiKey: String
        get() = _geminiApiKey
}