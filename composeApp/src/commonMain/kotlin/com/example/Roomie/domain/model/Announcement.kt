package com.example.Roomie.domain.model

data class Announcement(
    val id: String,
    val title: String,
    val message: String,
    val author: String,
    val createdAt: Long
)
