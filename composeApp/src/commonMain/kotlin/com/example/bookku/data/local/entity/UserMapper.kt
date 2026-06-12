package com.example.bookku.data.local.entity

import com.example.bookku.data.local.UserEntity
import com.example.bookku.domain.model.User
import kotlinx.datetime.Instant

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        avatarUrl = avatar_url ?: "",
        bio = bio ?: "",
        createdAt = Instant.fromEpochMilliseconds(created_at)
    )
}
