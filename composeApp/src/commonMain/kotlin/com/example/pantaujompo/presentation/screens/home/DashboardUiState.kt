package com.example.pantaujompo.presentation.screens.home

import com.example.pantaujompo.domain.model.ActivityModel

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val activities: List<ActivityModel>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
    data object Empty : DashboardUiState
}