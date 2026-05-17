package com.example.mybawanggacha.presentation.screens.library.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.library.model.LibraryEntry
import com.example.mybawanggacha.domain.library.model.LibraryStatus
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import com.example.mybawanggacha.presentation.screens.library.LibraryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow<LibraryStatus?>(null)
    val selectedStatus: StateFlow<LibraryStatus?> = _selectedStatus.asStateFlow()

    val uiState: StateFlow<LibraryUiState> = combine(
        libraryRepository.observeEntries(),
        _selectedStatus
    ) { entries, selectedStatus ->
        val filtered = selectedStatus?.let { status ->
            entries.filter { it.status == status }
        } ?: entries

        if (filtered.isEmpty()) {
            LibraryUiState.Empty(selectedStatus = selectedStatus)
        } else {
            LibraryUiState.Success(
                entries = filtered,
                selectedStatus = selectedStatus
            )
        }
    }
        .catch { error ->
            emit(LibraryUiState.Error(error.message ?: "Gagal memuat daftar anime/manga"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = LibraryUiState.Loading
        )

    fun selectStatus(status: LibraryStatus?) {
        _selectedStatus.value = status
    }

    fun deleteEntry(entry: LibraryEntry) {
        viewModelScope.launch {
            runCatching {
                libraryRepository.deleteEntry(entry.id)
            }
        }
    }
}