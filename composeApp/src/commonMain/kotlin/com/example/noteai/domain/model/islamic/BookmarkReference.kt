package com.example.noteai.domain.model.islamic

import kotlinx.datetime.Instant

data class BookmarkReference(
    val id: Long = 0,
    val referenceId: String,
    val sourceType: SourceType,
    val title: String,
    val sourceName: String,
    val arabicText: String,
    val translation: String,
    val explanation: String,
    val topicId: String,
    val topicTitle: String,
    val note: String = "",
    val savedAt: Instant
)
