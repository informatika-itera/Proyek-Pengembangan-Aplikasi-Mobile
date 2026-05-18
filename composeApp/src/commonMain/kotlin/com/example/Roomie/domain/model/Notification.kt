package com.example.Roomie.domain.model

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
