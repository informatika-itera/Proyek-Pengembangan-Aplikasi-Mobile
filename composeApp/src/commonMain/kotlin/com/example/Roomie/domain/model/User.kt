package com.example.Roomie.domain.model

enum class UserRole {
    STUDENT,
    ADMIN
}

data class User(
    val id: String,
    val name: String,
    val nim: String,
    val role: UserRole = UserRole.STUDENT
)
