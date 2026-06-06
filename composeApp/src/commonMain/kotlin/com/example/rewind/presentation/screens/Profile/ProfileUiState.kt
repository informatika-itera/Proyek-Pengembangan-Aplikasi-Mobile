package com.example.rewind.presentation.screens.profile

import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus

data class Achievement(
    val emoji: String,
    val title: String,
    val description: String,
    val unlocked: Boolean
)

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val totalMovies: Int,
        val averageRating: Float,
        val statusCounts: Map<WatchStatus, Int>,
        val topGenres: List<Pair<String, Int>>,
        val favoriteCount: Int,
        val achievements: List<Achievement>,
        val recentMovies: List<Movie>,
        val userName: String,
        val userBio: String,
        val currentStreak: Int = 0
    ) : ProfileUiState
}