package com.example.inventra.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.repository.BorrowRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(val records: List<BorrowRecord>) : HistoryUiState
    data object Empty : HistoryUiState
}

class HistoryViewModel(
    borrowRepository: BorrowRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = borrowRepository.getAllRecords()
        .map { records ->
            if (records.isEmpty()) HistoryUiState.Empty
            else HistoryUiState.Success(records)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState.Loading
        )
}
