package com.example.bridgebit.domain.model

data class Translation(
    val id: Long = 0,
    val sourceText: String,          // Teks asli yang diketik pengguna (menggantikan title)
    val translatedText: String,      // Hasil terjemahan atau output AI (menggantikan content)
    val sourceLanguage: String = "auto", // Bahasa asal (misal: "id", "en", atau "auto")
    val targetLanguage: String = "en",   // Bahasa tujuan (misal: "en", "ja")
    val isVaulted: Boolean = false,  // Status penyimpanan di Phrase Vault (Screen 5)
    val createdAt: Long,
    val updatedAt: Long
) {
    // Menampilkan cuplikan hasil terjemahan untuk list item di UI Dashboard
    val preview: String
        get() = if (translatedText.length > 100) translatedText.take(100) + "..." else translatedText

    // Mengecek apakah data teks terjemahan kosong
    val isEmpty: Boolean
        get() = sourceText.isBlank() && translatedText.isBlank()
}

/*

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
    GREEN(0xC8E6C9),
    BLUE(0xFFBBDEFB),
    PURPLE(0xFFE1BEE7);

    companion object {
        fun fromHex(hex: String): TranslationColor {
            return entries.find { it.name == hex } ?: DEFAULT
        }
    }
}
*/