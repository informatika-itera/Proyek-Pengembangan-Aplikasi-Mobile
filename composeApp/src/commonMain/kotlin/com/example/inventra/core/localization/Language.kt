package com.example.inventra.core.localization

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

enum class Language(val code: String, val displayName: String) {
    INDONESIAN("id", "Indonesia"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: INDONESIAN
        }
    }
}

val LocalLanguage = compositionLocalOf { mutableStateOf(Language.INDONESIAN) }

