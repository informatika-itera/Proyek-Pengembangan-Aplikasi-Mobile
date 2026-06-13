package com.example.bookku.data.local.entity

import com.example.bookku.data.local.ReadingProgress
import com.example.bookku.domain.model.ReadingProgress as ReadingProgressDomain
import com.example.bookku.domain.model.ReadingStatus
import kotlinx.datetime.Instant

fun ReadingProgress.toDomain(): ReadingProgressDomain {
    return ReadingProgressDomain(
        id = id,
        userId = user_id,
        bookId = book_id,
        currentPage = (current_page ?: 0L).toInt(),
        totalPages = (total_pages ?: 0L).toInt(),
        status = ReadingStatus.valueOf(status ?: "READING"),
        startedAt = Instant.fromEpochMilliseconds(started_at),
        finishedAt = finished_at?.let { Instant.fromEpochMilliseconds(it) },
        notes = notes ?: ""
    )
}

fun List<ReadingProgress>.toDomainList(): List<ReadingProgressDomain> {
    return map { it.toDomain() }
}