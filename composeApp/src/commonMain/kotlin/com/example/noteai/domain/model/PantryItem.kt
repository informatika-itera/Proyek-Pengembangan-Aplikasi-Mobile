package com.example.noteai.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class PantryItem(
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val unit: String,
    val category: String,
    val minStock: Double = 0.0,
    val updatedAt: Instant = Clock.System.now()
)
