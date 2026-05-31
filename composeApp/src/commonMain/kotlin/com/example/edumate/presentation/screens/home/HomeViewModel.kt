package com.example.edumate.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Enum baru untuk tipe filter tugas
enum class TaskFilter(val displayName: String) {
    ALL("Semua"),
    INCOMPLETE("Belum Selesai"),
    COMPLETED("Selesai")
}

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    // State untuk menyimpan teks pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // State untuk menyimpan filter yang sedang aktif
    private val _currentFilter = MutableStateFlow(TaskFilter.ALL)
    val currentFilter: StateFlow<TaskFilter> = _currentFilter

    // Mengombinasikan data dari database, teks pencarian, dan status filter
    val uiState: StateFlow<HomeUiState> = combine(
        refreshTrigger.flatMapLatest { repository.getAllTasks() },
        _searchQuery,
        _currentFilter
    ) { tasks, query, filter ->

        // Logika penyaringan
        val filteredTasks = tasks.filter { task ->
            val matchesQuery = task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.INCOMPLETE -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
            }

            matchesQuery && matchesFilter
        }

        // Penanganan tampilan UI sesuai hasil filter
        if (filteredTasks.isEmpty()) {
            if (tasks.isEmpty()) {
                HomeUiState.Empty
            } else {
                HomeUiState.Error("Tidak ada tugas yang cocok dengan filter atau pencarian.")
            }
        } else {
            HomeUiState.Success(filteredTasks)
        }
    }
        .catch { e ->
            emit(HomeUiState.Error(e.message ?: "Terjadi kesalahan sistem"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun refresh() {
        refreshTrigger.update { it + 1 }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filter: TaskFilter) {
        _currentFilter.value = filter
    }

    fun toggleTaskComplete(taskId: Long) {
        viewModelScope.launch {
            runCatching {
                repository.toggleComplete(taskId)
            }.onFailure {
                refresh()
            }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val tasks: List<Task>) : HomeUiState
    data object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
}