package com.example.pantaujompo.presentation.screens.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.domain.repository.ActivityRepository
import com.example.pantaujompo.presentation.screens.home.DashboardUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RiwayatViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    // Narik data dari database SQLDelight
    val uiState: StateFlow<DashboardUiState> = repository.getAllActivities()
        .map { activities ->
            if (activities.isEmpty()) DashboardUiState.Empty
            else DashboardUiState.Success(activities)
        }
        .onStart { DashboardUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )

    // Fungsi buat Hapus data
    fun deleteActivity(id: Long) {
        viewModelScope.launch {
            repository.deleteActivityById(id)
        }
    }
}