package com.example.noteai.domain.model

data class UserPreference(
    val userId: String = "",
    val favoriteGenres: List<String>,
    val savedGames: List<String>
)