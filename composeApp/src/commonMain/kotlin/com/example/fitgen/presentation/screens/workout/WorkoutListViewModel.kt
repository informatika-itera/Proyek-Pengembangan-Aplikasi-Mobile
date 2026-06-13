package com.example.fitgen.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.data.remote.api.ExerciseApiService
import com.example.fitgen.data.remote.dto.ExerciseDto
import com.example.fitgen.domain.model.CustomRoutine
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.repository.CustomRoutineRepository
import com.example.fitgen.domain.usecase.GetAllWorkoutsUseCase
import com.example.fitgen.domain.usecase.WorkoutSortBy
import com.example.fitgen.domain.repository.WorkoutRepository
import com.example.fitgen.core.network.NetworkResult
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

enum class WorkoutTimeFilter(val displayName: String) {
    SEMUA("Semua"),
    MINGGU_INI("Minggu Ini"),
    BULAN_INI("Bulan Ini")
}

data class WorkoutSummaryStats(
    val totalSessions: Int = 0,
    val totalVolume: Double = 0.0,
    val avgExercises: Double = 0.0
)

data class WeeklyProgressData(
    val dateLabel: String,
    val count: Int
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class WorkoutListViewModel(
    private val getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    private val repository: WorkoutRepository,
    private val customRoutineRepository: CustomRoutineRepository,
    private val exerciseApiService: ExerciseApiService
) : ViewModel() {

    private val _sortBy = MutableStateFlow(WorkoutSortBy.TANGGAL_TERBARU)
    val sortBy: StateFlow<WorkoutSortBy> = _sortBy

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _timeFilter = MutableStateFlow(WorkoutTimeFilter.SEMUA)
    val timeFilter: StateFlow<WorkoutTimeFilter> = _timeFilter

    private val _apiSearchResults = MutableStateFlow<List<ExerciseDto>>(emptyList())
    val apiSearchResults: StateFlow<List<ExerciseDto>> = _apiSearchResults

    val customRoutines: StateFlow<List<CustomRoutine>> = customRoutineRepository.getAllRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .collect { query ->
                    if (query.isNotBlank()) {
                        when (val result = exerciseApiService.searchExerciseByName(query)) {
                            is NetworkResult.Success -> _apiSearchResults.value = result.data
                            is NetworkResult.Error -> _apiSearchResults.value = emptyList()
                            is NetworkResult.Loading -> _apiSearchResults.value = emptyList()
                        }
                    } else {
                        _apiSearchResults.value = emptyList()
                    }
                }
        }
    }

    val uiState: StateFlow<WorkoutListUiState> = combine(
        _sortBy.flatMapLatest { getAllWorkoutsUseCase(it) },
        _searchQuery,
        _timeFilter,
        _sortBy
    ) { workouts, query, filter, sortBy ->
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // 1. Filter by Time
        val timeFiltered = workouts.filter { workout ->
            when (filter) {
                WorkoutTimeFilter.SEMUA -> true
                WorkoutTimeFilter.MINGGU_INI -> {
                    val daysDiff = today.toEpochDays() - workout.tanggal.toEpochDays()
                    daysDiff in 0..6
                }
                WorkoutTimeFilter.BULAN_INI -> {
                    workout.tanggal.year == today.year && workout.tanggal.month == today.month
                }
            }
        }

        // 2. Filter by Search Query
        val filtered = if (query.isBlank()) {
            timeFiltered
        } else {
            timeFiltered.filter { workout ->
                workout.gerakan.any { it.nama.contains(query, ignoreCase = true) } ||
                workout.catatan.contains(query, ignoreCase = true)
            }
        }

        // 3. Compute Stats based on filtered data
        val stats = WorkoutSummaryStats(
            totalSessions = filtered.size,
            totalVolume = filtered.sumOf { it.totalVolume },
            avgExercises = if (filtered.isEmpty()) 0.0 else filtered.sumOf { it.jumlahGerakan }.toDouble() / filtered.size
        )

        // 4. Compute Weekly Progress Chart Data (Always based on last 7 days, independent of search but bounded by workouts)
        val chartData = (6 downTo 0).map { daysAgo ->
            val date = today.minus(daysAgo, DateTimeUnit.DAY)
            val count = workouts.count { it.tanggal == date }
            val label = "${date.dayOfMonth}/${date.monthNumber}"
            WeeklyProgressData(label, count)
        }

        if (workouts.isEmpty() && query.isBlank() && filter == WorkoutTimeFilter.SEMUA) {
            WorkoutListUiState.Empty
        } else {
            WorkoutListUiState.Success(
                workouts = filtered,
                sortBy = sortBy,
                summaryStats = stats,
                weeklyChart = chartData,
                isEmptyResult = filtered.isEmpty()
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

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onTimeFilterChanged(filter: WorkoutTimeFilter) {
        _timeFilter.value = filter
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkout(id)
        }
    }

    fun deleteCustomRoutine(id: Long) {
        viewModelScope.launch {
            customRoutineRepository.deleteRoutine(id)
        }
    }

    fun createCustomRoutine(name: String) {
        viewModelScope.launch {
            customRoutineRepository.insertRoutine(name)
        }
    }

    fun removeExerciseFromRoutine(exerciseId: Long) {
        viewModelScope.launch {
            customRoutineRepository.removeExerciseFromRoutine(exerciseId)
        }
    }
}

// ─────────── UI State ───────────

sealed interface WorkoutListUiState {
    data object Loading : WorkoutListUiState
    data object Empty : WorkoutListUiState

    data class Success(
        val workouts: List<WorkoutLog>,
        val sortBy: WorkoutSortBy = WorkoutSortBy.TANGGAL_TERBARU,
        val summaryStats: WorkoutSummaryStats = WorkoutSummaryStats(),
        val weeklyChart: List<WeeklyProgressData> = emptyList(),
        val isEmptyResult: Boolean = false
    ) : WorkoutListUiState

    data class Error(val message: String) : WorkoutListUiState
}
