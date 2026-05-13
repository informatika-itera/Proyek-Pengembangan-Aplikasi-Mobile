package com.example.noteai.domain.model

data class Game(
    val id: String = "",
    val title: String,
    val genre: List<String>,
    val description: String,
    val releaseDate: String,
    val rating: Double,
    val imageUrl: String,
    val platform: List<String>
)