package com.example.inventra.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.domain.repository.BorrowRepository
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val totalItems: Int,
        val borrowedItems: Int,
        val overdueItems: Int,
        val activeBorrowings: List<BorrowRecord>
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    itemRepository: ItemRepository,
    borrowRepository: BorrowRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        itemRepository.getAllItems(),
        borrowRepository.getActiveRecords()
    ) { items, activeRecords ->
        val borrowedCount = items.count { it.availableStock < it.totalStock }
        val overdueCount = activeRecords.count { it.status == BorrowStatus.OVERDUE }
        
        DashboardUiState.Success(
            totalItems = items.size,
            borrowedItems = borrowedCount,
            overdueItems = overdueCount,
            activeBorrowings = activeRecords.take(5) // Just show top 5
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )
}
