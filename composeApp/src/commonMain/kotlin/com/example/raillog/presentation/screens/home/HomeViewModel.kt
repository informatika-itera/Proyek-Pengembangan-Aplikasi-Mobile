package com.example.raillog.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.domain.repository.SupplyRepository
import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.model.SupplyStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SupplyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Membaca user role secara reaktif dari DataStore
    val userRole: StateFlow<String> = userPreferences.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Operator Gudang")
    
    private val activeUsername = userPreferences.activeUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            activeUsername.collect { username ->
                loadDashboardData(username)
            }
        }
    }

    private fun loadDashboardData(username: String) {
        viewModelScope.launch {
            repository.getAllItems(username)
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Terjadi kesalahan sistem")
                }
                .collect { items ->
                    if (items.isEmpty()) {
                        _uiState.value = HomeUiState.Empty
                    } else {
                        val total = items.size
                        val critical = items.count { it.priority == Priority.CRITICAL }
                        val pending = items.count { it.status == SupplyStatus.PENDING }
                        _uiState.value = HomeUiState.Success(
                            totalItems = total,
                            criticalItems = critical,
                            pendingItems = pending,
                            recentItems = items
                        )
                    }
                }
        }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(
        val totalItems: Int,
        val criticalItems: Int,
        val pendingItems: Int,
        val recentItems: List<com.example.raillog.domain.model.SupplyItem>
    ) : HomeUiState
}