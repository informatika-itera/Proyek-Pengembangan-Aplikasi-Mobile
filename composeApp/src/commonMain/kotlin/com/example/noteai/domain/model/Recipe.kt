package com.example.noteai.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Recipe(
    val id: Long = 0,
    val title: String,
    val ingredients: String,
    val instructions: String,
    val isFavorite: Boolean = false,
    val isAiGenerated: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
