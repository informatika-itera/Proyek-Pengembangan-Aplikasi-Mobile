package com.example.bookku.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ReadingProgress(
    val id: Long = 0,
    val userId: String,
    val bookId: Long,
    val currentPage: Int = 0,
    val totalPages: Int,
    val status: ReadingStatus = ReadingStatus.READING,
    val startedAt: Instant = Clock.System.now(),
    val finishedAt: Instant? = null,
    val notes: String = ""
) {
    val progress: Float
        get() = if (totalPages > 0) (currentPage.toFloat() / totalPages).coerceIn(0f, 1f) else 0f
    
    val progressPercent: Int
        get() = (progress * 100).toInt()
}

enum class ReadingStatus(val displayName: String) {
    PLANNING("Akan Dibaca"),
    READING("Sedang Baca"),
    COMPLETED("Selesai"),
    ABANDONED("Dibatalkan")
}
