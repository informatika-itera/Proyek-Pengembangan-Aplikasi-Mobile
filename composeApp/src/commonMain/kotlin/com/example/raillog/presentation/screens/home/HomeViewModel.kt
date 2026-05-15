package com.example.raillog.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val supplyRepository: SupplyRepository
) : ViewModel() {

    // Mengambil stream data dari database lokal dan memetakannya menjadi UI State
    val uiState: StateFlow<HomeUiState> = supplyRepository.getAllItems()
        .map { items ->
            if (items.isEmpty()) {
                HomeUiState.Empty
            } else {
                HomeUiState.Success(
                    recentItems = items.take(10), // Menampilkan 10 item terbaru
                    totalItems = items.size,
                    criticalItems = items.count { it.priority.name == "CRITICAL" },
                    pendingItems = items.count { it.status.name == "PENDING" }
                )
            }
        }
        .catch { emit(HomeUiState.Error(it.message ?: "Terjadi kesalahan sistem")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )
}

// Sealed interface untuk mengelola berbagai state pada layar Dashboard
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(
        val recentItems: List<SupplyItem>,
        val totalItems: Int,
        val criticalItems: Int,
        val pendingItems: Int
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}