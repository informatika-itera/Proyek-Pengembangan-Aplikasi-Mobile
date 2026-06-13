package com.example.noteai.domain.model

data class User(
    val id: Long = 0,
    val email: String,
    val name: String,
    val password: String,
    val isLoggedIn: Boolean = false
)
