package com.example.mybawanggacha.data.remote.jikan.api

class JikanApiException(
    val statusCode: Int,
    val type: String,
    override val message: String,
    val error: String? = null
) : Exception(message) {
    val isRateLimited: Boolean
        get() = statusCode == 429 || type.equals("RateLimitException", ignoreCase = true)
}

class JikanNotModifiedException : Exception("Jikan response was not modified")
