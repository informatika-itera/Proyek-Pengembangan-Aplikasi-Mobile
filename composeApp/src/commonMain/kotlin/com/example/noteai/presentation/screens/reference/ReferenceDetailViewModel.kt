package com.example.noteai.presentation.screens.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.islamic.IslamicReference
import com.example.noteai.domain.repository.hujjah.BookmarkRepository
import com.example.noteai.domain.repository.hujjah.HujjahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ReferenceDetailUiState(
    val isLoading: Boolean = true,
    val reference: IslamicReference? = null,
    val isBookmarked: Boolean = false,
    val note: String = "",
    val errorMessage: String? = null
)

class ReferenceDetailViewModel(
    private val referenceId: String,
    private val hujjahRepository: HujjahRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReferenceDetailUiState())
    val uiState: StateFlow<ReferenceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            try {
                combine(
                    hujjahRepository.getReferenceById(referenceId),
                    bookmarkRepository.getBookmarkByReferenceId(referenceId)
                ) { reference, bookmark ->
                    ReferenceDetailUiState(
                        isLoading = false,
                        reference = reference,
                        isBookmarked = bookmark != null,
                        note = bookmark?.note.orEmpty(),
                        errorMessage = if (reference == null) "Referensi tidak ditemukan" else null
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = ReferenceDetailUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat detail referensi"
                )
            }
        }
    }

    fun onNoteChanged(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun saveBookmark() {
        val reference = _uiState.value.reference ?: return
        viewModelScope.launch {
            bookmarkRepository.saveBookmark(reference, _uiState.value.note)
        }
    }

    fun updateNote() {
        val reference = _uiState.value.reference ?: return
        viewModelScope.launch {
            bookmarkRepository.updateBookmarkNote(reference.id, _uiState.value.note)
        }
    }

    fun deleteBookmark() {
        val reference = _uiState.value.reference ?: return
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(reference.id)
        }
    }
}
