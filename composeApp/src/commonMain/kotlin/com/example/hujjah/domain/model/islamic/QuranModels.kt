package com.example.hujjah.domain.model.islamic

import kotlinx.serialization.Serializable

@Serializable
data class SurahItem(
    val number: Int,
    val name: String,
    val translation: String,
    val numberOfVerses: Int,
    val revelation: String,
    val asma: String
)

@Serializable
data class VerseItem(
    val number: Int,
    val arabic: String,
    val translation: String
)
