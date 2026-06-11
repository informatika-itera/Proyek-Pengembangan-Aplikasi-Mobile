package com.example.hujjah.domain.model.islamic

import kotlinx.serialization.Serializable

@Serializable
data class IslamicReference(
    val id: String,
    val sourceType: SourceType,
    val title: String,
    val sourceName: String,
    val arabicText: String,
    val translation: String,
    val explanation: String,
    val topicId: String,
    val topicTitle: String,
    val surahNumber: Int? = null,
    val verseNumber: Int? = null
)
