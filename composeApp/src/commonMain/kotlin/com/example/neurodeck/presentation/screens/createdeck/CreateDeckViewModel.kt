package com.example.neurodeck.presentation.screens.createdeck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State untuk CreateDeck screen.
 *
 * Field state separated: title, description, error message, isSaving flag.
 * canSave = title non-blank && !isSaving (derived property).
 *
 * Tidak pakai sealed interface karena screen ini PURE FORM (tidak ada
 * loading/empty/error state list — selalu render form, error sebagai inline
 * field hint).
 */
data class CreateDeckUiState(
    val title: String = "",
    val description: String = "",
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
) {
    /** Form valid + tidak sedang save. Tombol Lanjutkan enabled berdasarkan ini. */
    val canSave: Boolean
        get() = title.trim().length >= MIN_TITLE_LENGTH && !isSaving

    companion object {
        const val MIN_TITLE_LENGTH = 3
        const val MAX_TITLE_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 500
    }
}

/**
 * ViewModel untuk CreateDeck screen.
 *
 * Tugas: validate input, persist deck baru ke DB, return ID via callback
 * supaya UI bisa navigate (ke ImportGenerate atau ke CardList).
 *
 * NOTE: ViewModel TIDAK tahu navigation target — itu UI layer responsibility.
 * Method `saveDeck` cuma return ID via callback, caller (Screen composable)
 * yang decide route berdasarkan user choice (Manual / AI Generate).
 */
class CreateDeckViewModel(
    private val deckRepository: DeckRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateDeckUiState())
    val uiState: StateFlow<CreateDeckUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        // Limit length supaya tidak overflow UI / DB.
        val trimmed = value.take(CreateDeckUiState.MAX_TITLE_LENGTH)
        _uiState.update { it.copy(title = trimmed, errorMessage = null) }
    }

    fun onDescriptionChange(value: String) {
        val trimmed = value.take(CreateDeckUiState.MAX_DESCRIPTION_LENGTH)
        _uiState.update { it.copy(description = trimmed, errorMessage = null) }
    }

    /**
     * Persist deck baru ke DB, lalu return ID via callback.
     *
     * @param onSuccess  Dipanggil dengan deckId baru saat save berhasil.
     *                   Caller harus navigate sesuai user choice di sini.
     */
    fun saveDeck(onSuccess: (deckId: Long) -> Unit) {
        val current = _uiState.value
        if (!current.canSave) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val deckId = deckRepository.createDeck(
                    title = current.title.trim(),
                    description = current.description.trim(),
                )
                onSuccess(deckId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Gagal menyimpan deck",
                    )
                }
            }
        }
    }
}