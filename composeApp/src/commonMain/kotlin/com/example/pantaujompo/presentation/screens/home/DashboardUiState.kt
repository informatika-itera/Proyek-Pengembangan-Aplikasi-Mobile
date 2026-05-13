package com.example.pantaujompo.presentation.screens.home

// Nanti kita ganti Any dengan model Activity murni kita
sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val activities: List<Any>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
    data object Empty : DashboardUiState
}