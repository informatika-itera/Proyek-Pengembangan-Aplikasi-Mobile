package com.example.bridgebit.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Translation(
    val id: Long = 0,
    val sourceText: String,      // Teks asal
    val translatedText: String,  // Teks hasil terjemahan
    val sourceLanguage: String = "auto",
    val targetLanguage: String = "en",
    val isVaulted: Boolean = false, // Status tersimpan di Phrase Vault
    val createdAt: Long,
    val updatedAt: Long
) {
    // Menampilkan cuplikan hasil terjemahan untuk di UI
    val preview: String
        get() = if (translatedText.length > 100) translatedText.take(100) + "..." else translatedText

    // Mengecek apakah kosong
    val isEmpty: Boolean
        get() = sourceText.isBlank() && translatedText.isBlank()
}

enum class TranslationCategory(val displayName: String) {
    GENERAL("Umum"),
    WORK("Pekerjaan"),
    PERSONAL("Pribadi"),
    IDEAS("Ide"),
    TODO("To-Do"),
    STUDY("Belajar");
    
    companion object {
        fun fromString(value: String): TranslationCategory {
            return entries.find { it.name == value } ?: GENERAL
        }
    }
}

enum class TranslationColor(val hexValue: Long) {
    DEFAULT(0xFFFFFFFF),
    RED(0xFFFFCDD2),
    ORANGE(0xFFFFE0B2),
    YELLOW(0xFFFFF9C4),
    GREEN(0xFFC8E6C9),
    BLUE(0xFFBBDEFB),
    PURPLE(0xFFE1BEE7),
    PINK(0xFFF8BBD9);
    
    companion object {
        fun fromString(value: String): TranslationColor {
            return entries.find { it.name == value } ?: DEFAULT
        }
    }
}
