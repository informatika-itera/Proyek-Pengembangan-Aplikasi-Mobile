package com.example.fitgen.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.usecase.GetAllWorkoutsUseCase
import com.example.fitgen.domain.usecase.WorkoutSortBy
import com.example.fitgen.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutListViewModel(
    private val getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _sortBy = MutableStateFlow(WorkoutSortBy.TANGGAL_TERBARU)
    val sortBy: StateFlow<WorkoutSortBy> = _sortBy

    val uiState: StateFlow<WorkoutListUiState> = _sortBy
        .flatMapLatest { sortBy ->
            getAllWorkoutsUseCase(sortBy)
        }
        .combine(_sortBy) { workouts, sortBy ->
            when {
                workouts.isEmpty() -> WorkoutListUiState.Empty
                else -> WorkoutListUiState.Success(
                    workouts = workouts,
                    sortBy = sortBy
                )
            }
        }
        .catch { e ->
            emit(WorkoutListUiState.Error(e.message ?: "Terjadi kesalahan"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorkoutListUiState.Loading
        )

    // ─────────── User Actions ───────────

    fun onSortByChanged(sortBy: WorkoutSortBy) {
        _sortBy.value = sortBy
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkout(id)
        }
    }
}

// ─────────── UI State ───────────

sealed interface WorkoutListUiState {
    data object Loading : WorkoutListUiState
    data object Empty : WorkoutListUiState

    data class Success(
        val workouts: List<WorkoutLog>,
        val sortBy: WorkoutSortBy = WorkoutSortBy.TANGGAL_TERBARU
    ) : WorkoutListUiState

    data class Error(val message: String) : WorkoutListUiState
}
