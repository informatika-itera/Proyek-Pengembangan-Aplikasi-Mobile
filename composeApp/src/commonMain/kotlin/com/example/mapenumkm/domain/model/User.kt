package com.example.mapenumkm.domain.model

data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val createdAt: Long
)
