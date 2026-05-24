package com.example.noteai.data.local.entity

import com.example.noteai.data.local.NoteEntity
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.NoteColor
import com.example.noteai.domain.model.VulnSeverity
import com.example.noteai.domain.model.VulnStatus
import com.example.noteai.domain.model.VulnType
import kotlinx.datetime.Instant

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        targetUrl = target_url,
        vulnType = VulnType.fromString(vuln_type),
        color = NoteColor.fromString(color),
        severity = VulnSeverity.fromString(severity),
        status = VulnStatus.fromString(status),
        isPinned = is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

data class NoteEntityValues(
    val title: String,
    val content: String,
    val targetUrl: String,
    val vulnType: String,
    val color: String,
    val severity: String,
    val status: String,
    val isPinned: Long,
    val createdAt: Long,
    val updatedAt: Long
)

fun Note.toEntityValues(): NoteEntityValues {
    return NoteEntityValues(
        title = title,
        content = content,
        targetUrl = targetUrl,
        vulnType = vulnType.name,
        color = color.name,
        severity = severity.name,
        status = status.name,
        isPinned = if (isPinned) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun List<NoteEntity>.toDomainList(): List<Note> {
    return map { it.toDomain() }
}