package com.example.tripmate.domain.model

data class PackingItem(
    val id: Long = 0,
    val tripId: Long,
    val name: String,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
