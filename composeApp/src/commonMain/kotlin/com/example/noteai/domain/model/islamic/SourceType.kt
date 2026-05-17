package com.example.noteai.domain.model.islamic

enum class SourceType {
    QURAN,
    HADITH;

    fun label(): String = when (this) {
        QURAN -> "Al-Qur'an"
        HADITH -> "Hadis"
    }
}
