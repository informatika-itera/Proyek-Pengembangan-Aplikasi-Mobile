package com.example.bookku.data.local.entity

import com.example.bookku.data.local.Comment
import com.example.bookku.domain.model.Comment as CommentDomain
import kotlinx.datetime.Instant

fun Comment.toDomain(): CommentDomain {
    return CommentDomain(
        id = id,
        bookId = book_id,
        userId = user_id,
        userName = user_name,
        content = content,
        likesCount = (likes_count ?: 0L).toInt(),
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at),
        parentCommentId = parent_comment_id
    )
}

fun List<Comment>.toDomainList(): List<CommentDomain> {
    return map { it.toDomain() }
}