package com.example.hujjah.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Umum",
    val color: String = "DEFAULT",
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() = if (content.length > 100) content.take(100) + "..." else content
    
    val isEmpty: Boolean
        get() = title.isBlank() && content.isBlank()
}
