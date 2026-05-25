package com.example.tabungin.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.DeleteTargetUseCase
import com.example.tabungin.domain.usecase.GetAllTargetsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class FilterType {
    ALL, NAMA, DEADLINE, PROGRESS
}

enum class SortOrder {
    TERBARU, TERLAMA, NAMA_AZ, NAMA_ZA, PROGRESS_TERTINGGI, PROGRESS_TERENDAH
}

data class HomeUiState(
    val targets: List<Target> = emptyList(),
    val filteredTargets: List<Target> = emptyList(),
    val isLoading: Boolean    = true,
    val error: String?       = null,
    val totalTerkumpul: Double = 0.0,
    val totalTarget: Double   = 0.0,
    val searchQuery: String = "",
    val filterType: FilterType = FilterType.ALL,
    val sortOrder: SortOrder = SortOrder.TERBARU
)

class HomeViewModel(
    private val getAllTargetsUseCase: GetAllTargetsUseCase,
    private val deleteTargetUseCase: DeleteTargetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow(FilterType.ALL)
    private val _sortOrder = MutableStateFlow(SortOrder.TERBARU)

    init { observeTargets() }

    private fun observeTargets() {
        combine(
            getAllTargetsUseCase(),
            _searchQuery,
            _filterType,
            _sortOrder
        ) { targets, query, filterType, sortOrder ->
            val filtered = applyFilters(targets, query, filterType)
            val sorted = applySorting(filtered, sortOrder)
            Triple(targets, sorted, sortOrder)
        }
            .onEach { (allTargets, filteredTargets, _) ->
                _uiState.update {
                    it.copy(
                        targets        = allTargets,
                        filteredTargets = filteredTargets,
                        isLoading      = false,
                        totalTerkumpul = allTargets.sumOf { t -> t.terkumpul },
                        totalTarget    = allTargets.sumOf { t -> t.targetAmount }
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    private fun applyFilters(targets: List<Target>, query: String, filterType: FilterType): List<Target> {
        var result = targets

        // Apply search query
        if (query.isNotBlank()) {
            result = result.filter { target ->
                target.nama.contains(query, ignoreCase = true)
            }
        }

        // Apply filter type
        result = when (filterType) {
            FilterType.ALL -> result
            FilterType.NAMA -> result.sortedBy { it.nama }
            FilterType.DEADLINE -> {
                result.filter { target ->
                    val deadlineParts = target.deadline.split("-")
                    if (deadlineParts.size == 3) {
                        val month = deadlineParts[1].toIntOrNull() ?: return@filter false
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val currentMonth = now.monthNumber
                        val diff = month - currentMonth
                        diff in 0..3
                    } else false
                }
            }
            FilterType.PROGRESS -> {
                result.filter { it.progres >= 0.5 }
            }
        }

        return result
    }

    private fun applySorting(targets: List<Target>, sortOrder: SortOrder): List<Target> {
        return when (sortOrder) {
            SortOrder.TERBARU -> targets.sortedByDescending { it.id }
            SortOrder.TERLAMA -> targets.sortedBy { it.id }
            SortOrder.NAMA_AZ -> targets.sortedBy { it.nama.lowercase() }
            SortOrder.NAMA_ZA -> targets.sortedByDescending { it.nama.lowercase() }
            SortOrder.PROGRESS_TERTINGGI -> targets.sortedByDescending { it.progres }
            SortOrder.PROGRESS_TERENDAH -> targets.sortedBy { it.progres }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFilterTypeChange(filterType: FilterType) {
        _filterType.value = filterType
        _uiState.update { it.copy(filterType = filterType) }
    }

    fun onSortOrderChange(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _filterType.value = FilterType.ALL
        _sortOrder.value = SortOrder.TERBARU
        _uiState.update {
            it.copy(
                searchQuery = "",
                filterType = FilterType.ALL,
                sortOrder = SortOrder.TERBARU
            )
        }
    }

    fun deleteTarget(id: Long) {
        viewModelScope.launch {
            runCatching { deleteTargetUseCase(id) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
