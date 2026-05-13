package com.example.raillog.presentation.screens.home

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    // Akan diimplementasi Sprint 2
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
}