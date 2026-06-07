package com.example.bridgebit.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.usecase.DeleteTranslationUseCase
import com.example.bridgebit.domain.usecase.SearchHistoryUseCase
import com.example.bridgebit.domain.usecase.ToggleVaultStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data class Success(val history: List<Translation>) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

// STRUKTUR DATA BARU UNTUK FILTER MULTIGUNA
data class FilterState(
    val isVaultOnly: Boolean = false,
    val selectedLanguage: String? = null, // null berarti "Semua Bahasa"
    val selectedCategory: String? = null  // null berarti "Semua Kategori"
)

class DashboardViewModel(
    private val searchHistoryUseCase: SearchHistoryUseCase,
    private val deleteTranslationUseCase: DeleteTranslationUseCase,
    private val toggleVaultStatusUseCase: ToggleVaultStatusUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState = _filterState.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = combine(
        _searchQuery
            .debounce(300)
            .flatMapLatest { query -> searchHistoryUseCase(query) },
        _filterState
    ) { history, filter ->

        // LOGIKA PENYARINGAN BERTINGKAT
        var filteredHistory = history

        if (filter.isVaultOnly) {
            filteredHistory = filteredHistory.filter { it.isVaulted }
        }

        if (filter.selectedLanguage != null) {
            filteredHistory = filteredHistory.filter {
                it.sourceLanguage == filter.selectedLanguage || it.targetLanguage == filter.selectedLanguage
            }
        }

        if (filter.selectedCategory != null) {
            filteredHistory = filteredHistory.filter { it.category == filter.selectedCategory }
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

    // FUNGSI-FUNGSI BARU UNTUK MENGUBAH STATUS FILTER
    fun toggleVaultFilter() {
        _filterState.update { it.copy(isVaultOnly = !it.isVaultOnly) }
    }

    fun setLanguageFilter(language: String?) {
        _filterState.update { it.copy(selectedLanguage = language) }
    }

    fun setCategoryFilter(category: String?) {
        _filterState.update { it.copy(selectedCategory = category) }
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun deleteTranslation(id: Long) {
        viewModelScope.launch {
            deleteTranslationUseCase(id)
        }
    }

    fun toggleVaultStatus(id: Long) {
        viewModelScope.launch {
            toggleVaultStatusUseCase(id)
        }
    }
}