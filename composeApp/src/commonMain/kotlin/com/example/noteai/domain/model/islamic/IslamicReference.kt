package com.example.noteai.domain.model.islamic

data class IslamicReference(
    val id: String,
    val sourceType: SourceType,
    val title: String,
    val sourceName: String,
    val arabicText: String,
    val translation: String,
    val explanation: String,
    val topicId: String,
    val topicTitle: String
)
