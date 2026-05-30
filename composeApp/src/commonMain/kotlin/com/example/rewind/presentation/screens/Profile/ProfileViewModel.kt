package com.example.rewind.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewind.data.local.datastore.UserPreferences
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.usecase.GetAllMoviesUseCase
import com.example.rewind.domain.usecase.GetFavoriteMoviesUseCase
import com.example.rewind.domain.usecase.MovieSortBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getAllMovies: GetAllMoviesUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Edit mode state
    val isEditMode = MutableStateFlow(false)
    val editName = MutableStateFlow("")
    val editBio = MutableStateFlow("")

    init { loadStats() }

    private fun loadStats() {
        viewModelScope.launch {
            combine(
                getAllMovies(MovieSortBy.UPDATED_DESC),
                userPreferences.userName,
                userPreferences.userBio
            ) { movies, name, bio ->
                Triple(movies, name, bio)
            }
                .catch { _uiState.value = ProfileUiState.Loading }
                .collect { (movies, name, bio) ->
                    val statusCounts = WatchStatus.entries.associateWith { status ->
                        movies.count { it.status == status }
                    }
                    val avgRating = movies.mapNotNull { it.rating }.let { ratings ->
                        if (ratings.isEmpty()) 0f
                        else (ratings.sum() / ratings.size * 10).toInt() / 10f
                    }
                    val topGenres = movies
                        .groupBy { it.genre.displayName }
                        .map { it.key to it.value.size }
                        .sortedByDescending { it.second }

                    val favoriteCount = movies.count { (it.rating ?: 0f) >= 4f }
                    val completedCount = statusCounts[WatchStatus.COMPLETED] ?: 0

                    val achievements = listOf(
                        Achievement("🎬", "First Frame", "Add your first title", movies.isNotEmpty()),
                        Achievement("📚", "Collector", "Add 10 titles", movies.size >= 10),
                        Achievement("✅", "Finisher", "Complete 5 titles", completedCount >= 5),
                        Achievement("⭐", "Critic", "Rate 5 titles", favoriteCount >= 5),
                        Achievement("🎭", "Genre Hopper", "Watch 3 different genres", topGenres.size >= 3),
                        Achievement("💯", "Century", "Add 100 titles", movies.size >= 100)
                    )

                    // Sync edit fields kalau belum di-edit
                    if (!isEditMode.value) {
                        editName.value = name.ifBlank { "Rewind User" }
                        editBio.value = bio.ifBlank { "Film enthusiast & series binger 🍿" }
                    }

                    _uiState.value = ProfileUiState.Success(
                        totalMovies = movies.size,
                        averageRating = avgRating,
                        statusCounts = statusCounts,
                        topGenres = topGenres,
                        favoriteCount = favoriteCount,
                        achievements = achievements,
                        recentMovies = movies.take(5),
                        userName = name.ifBlank { "Rewind User" },
                        userBio = bio.ifBlank { "Film enthusiast & series binger 🍿" }
                    )
                }
        }
    }

    fun startEdit() {
        isEditMode.value = true
    }

    fun saveEdit() {
        viewModelScope.launch {
            userPreferences.setUserName(editName.value.trim().ifBlank { "Rewind User" })
            userPreferences.setUserBio(editBio.value.trim().ifBlank { "Film enthusiast & series binger 🍿" })
            isEditMode.value = false
        }
    }

    fun cancelEdit() {
        val state = _uiState.value
        if (state is ProfileUiState.Success) {
            editName.value = state.userName
            editBio.value = state.userBio
        }
        isEditMode.value = false
    }
}