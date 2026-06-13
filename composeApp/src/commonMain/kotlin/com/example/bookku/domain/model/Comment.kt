package com.example.bookku.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Comment(
    val id: Long = 0,
    val bookId: Long,
    val userId: String,
    val userName: String,
    val content: String,
    val likesCount: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val parentCommentId: Long? = null
)
