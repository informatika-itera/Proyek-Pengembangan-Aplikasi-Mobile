package com.kelazzz.app.domain.model

/**
 * Domain model untuk data user yang sudah login
 */
data class User(
    val userId: String,
    val nama: String,
    val nim: String,
    val email: String,
    val unit: String,
    val level: String,
    val photoUrl: String,
    val token: String
)
