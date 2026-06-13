package com.example.bookku.domain.model

import kotlinx.datetime.Instant

data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String = "",
    val bio: String = "",
    val createdAt: Instant
)
