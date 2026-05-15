package com.dailybliss.app.data.local.entity

import com.dailybliss.app.data.local.MomentEntity
import com.dailybliss.app.domain.model.Moment
import kotlinx.datetime.Instant

fun MomentEntity.toDomain(): Moment {
    return Moment(
        id = id,
        title = title,
        content = content,
        imageUrl = media_url,
        isPinned = is_pinned == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

data class MomentEntityValues(
    val title: String,
    val content: String,
    val mediaUrl: String?,
    val isPinned: Long,
    val createdAt: Long,
    val updatedAt: Long
)

fun Moment.toEntityValues(): MomentEntityValues {
    return MomentEntityValues(
        title = title,
        content = content,
        mediaUrl = imageUrl,
        isPinned = if (isPinned) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}

fun List<MomentEntity>.toDomainList(): List<Moment> {
    return map { it.toDomain() }
}

