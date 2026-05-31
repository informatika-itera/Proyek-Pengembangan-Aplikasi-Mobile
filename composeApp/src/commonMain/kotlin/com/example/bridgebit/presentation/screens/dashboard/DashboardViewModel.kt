package com.example.bridgebit.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.usecase.DeleteTranslationUseCase
import com.example.bridgebit.domain.usecase.SearchHistoryUseCase
import com.example.bridgebit.domain.usecase.ToggleVaultStatusUseCase // <-- Import baharu
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data class Success(val history: List<Translation>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    private val searchHistoryUseCase: SearchHistoryUseCase,
    private val deleteTranslationUseCase: DeleteTranslationUseCase,
    private val toggleVaultStatusUseCase: ToggleVaultStatusUseCase // <-- Parameter baharu
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow("Semua")
    val activeFilter = _activeFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = combine(
        _searchQuery.flatMapLatest { query -> searchHistoryUseCase(query) },
        _activeFilter
    ) { history, filter ->
        val filteredHistory = when (filter) {
            "Vault" -> history.filter { it.isVaulted }
            "Indonesia" -> history.filter { it.sourceLanguage == "Indonesia" || it.targetLanguage == "Indonesia" }
            "Inggris" -> history.filter { it.sourceLanguage == "Inggris" || it.targetLanguage == "Inggris" }
            else -> history
        }

        if (filteredHistory.isEmpty()) DashboardUiState.Empty
        else DashboardUiState.Success(filteredHistory)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: String) {
        _activeFilter.value = filter
    }

    fun deleteTranslation(id: Long) {
        viewModelScope.launch {
            deleteTranslationUseCase(id)
        }
    }

    // <-- FUNGSI BAHARU UNTUK VAULT -->
    fun toggleVaultStatus(id: Long) {
        viewModelScope.launch {
            toggleVaultStatusUseCase(id)
        }
    }
}