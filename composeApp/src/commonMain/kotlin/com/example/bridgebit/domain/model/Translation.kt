package com.example.bridgebit.domain.model

data class Translation(
    val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String = "auto",
    val targetLanguage: String = "en",
    val category: String = "Umum", // <-- KOLOM BARU
    val isVaulted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
) {
    val preview: String
        get() = if (translatedText.length > 100) translatedText.take(100) + "..." else translatedText

    val isEmpty: Boolean
        get() = sourceText.isBlank() && translatedText.isBlank()
}