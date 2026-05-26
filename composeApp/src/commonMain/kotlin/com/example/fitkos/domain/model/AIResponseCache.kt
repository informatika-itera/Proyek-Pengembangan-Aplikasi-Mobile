package com.example.fitkos.domain.model

data class AIResponseCache(
    val prompt: String = "",
    val response: String = "",
    val updatedAt: String = ""
) {
    val isAvailable: Boolean
        get() = prompt.isNotBlank() && response.isNotBlank()
}