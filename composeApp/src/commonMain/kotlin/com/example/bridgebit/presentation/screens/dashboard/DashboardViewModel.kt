package com.example.bridgebit.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.usecase.GetAllHistoryUseCase
import com.example.bridgebit.domain.usecase.DeleteTranslationUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    object Empty : DashboardUiState
    data class Success(val history: List<Translation>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val deleteTranslationUseCase: DeleteTranslationUseCase
) : ViewModel() {

    // Mengambil data riwayat dari database lokal secara real-time
    val uiState: StateFlow<DashboardUiState> = getAllHistoryUseCase()
        .map { history ->
            if (history.isEmpty()) DashboardUiState.Empty
            else DashboardUiState.Success(history)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )

    fun deleteTranslation(id: Long) {
        viewModelScope.launch {
            deleteTranslationUseCase(id)
        }
    }
}