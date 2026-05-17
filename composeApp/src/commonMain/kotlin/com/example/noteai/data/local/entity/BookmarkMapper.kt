package com.example.noteai.data.local.entity

import com.example.noteai.data.local.BookmarkEntity
import com.example.noteai.domain.model.islamic.BookmarkReference
import com.example.noteai.domain.model.islamic.IslamicReference
import com.example.noteai.domain.model.islamic.SourceType
import kotlinx.datetime.Instant
import kotlin.collections.map

fun BookmarkEntity.toDomain(): BookmarkReference {
    return BookmarkReference(
        id = id,
        referenceId = reference_id,
        sourceType = SourceType.valueOf(source_type),
        title = title,
        sourceName = source_name,
        arabicText = arabic_text,
        translation = translation,
        explanation = explanation,
        topicId = topic_id,
        topicTitle = topic_title,
        note = note,
        savedAt = Instant.fromEpochMilliseconds(saved_at)
    )
}

fun List<BookmarkEntity>.toBookmarkDomainList(): List<BookmarkReference> {
    return map { it.toDomain() }
}

data class BookmarkEntityValues(
    val referenceId: String,
    val sourceType: String,
    val title: String,
    val sourceName: String,
    val arabicText: String,
    val translation: String,
    val explanation: String,
    val topicId: String,
    val topicTitle: String,
    val note: String,
    val savedAt: Long
)

fun IslamicReference.toBookmarkEntityValues(
    note: String,
    savedAt: Long
): BookmarkEntityValues {
    return BookmarkEntityValues(
        referenceId = id,
        sourceType = sourceType.name,
        title = title,
        sourceName = sourceName,
        arabicText = arabicText,
        translation = translation,
        explanation = explanation,
        topicId = topicId,
        topicTitle = topicTitle,
        note = note,
        savedAt = savedAt
    )
}
