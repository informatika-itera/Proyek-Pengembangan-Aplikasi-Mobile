package com.example.bookku.presentation.screens.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.domain.model.ReadingProgress
import com.example.bookku.domain.repository.AuthRepository
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.repository.ReadingProgressRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(
    private val progressRepository: ReadingProgressRepository,
    private val bookRepository: NoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrackerUiState>(TrackerUiState.Loading)
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    init {
        loadTrackerData()
    }

    private fun loadTrackerData() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            progressRepository.getAllReadingProgress(userId)
                .flatMapLatest { progresses ->
                    if (progresses.isEmpty()) {
                        flowOf(TrackerUiState.Empty)
                    } else {
                        val bookFlows = progresses.map { progress ->
                            bookRepository.getBookById(progress.bookId).map { book ->
                                book?.let {
                                    TrackerItem(it.title, it.author, it.coverUrl, progress)
                                }
                            }
                        }
                        
                        combine(bookFlows) { items ->
                            val validItems = items.filterNotNull()
                            if (validItems.isEmpty()) TrackerUiState.Empty 
                            else TrackerUiState.Success(validItems)
                        }
                    }
                }
                .catch { e ->
                    _uiState.value = TrackerUiState.Error(e.message ?: "Gagal memuat data tracker")
                }
                .collect {
                    _uiState.value = it
                }
        }
    }
}

data class TrackerItem(
    val title: String,
    val author: String,
    val coverUrl: String,
    val progress: ReadingProgress
)

sealed interface TrackerUiState {
    data object Loading : TrackerUiState
    data class Success(val items: List<TrackerItem>) : TrackerUiState
    data object Empty : TrackerUiState
    data class Error(val message: String) : TrackerUiState
}
