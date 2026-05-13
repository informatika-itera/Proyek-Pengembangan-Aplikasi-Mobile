package com.example.gamenews.domain.model

data class Game(
    val id: Int,
    val title: String,
    val description: String,
    val genre: String,
    val rating: Double,
    val imageUrl: String?
)