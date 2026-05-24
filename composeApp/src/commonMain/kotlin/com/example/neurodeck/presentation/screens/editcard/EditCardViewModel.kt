package com.example.neurodeck.presentation.screens.editcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state untuk EditCard screen.
 *
 * Memiliki 3 sub-state implisit via field combinations:
 * - isLoading=true        → loading existing card data
 * - errorMessage != null  → error (gagal load atau gagal save)
 * - lainnya               → ready / saving
 *
 * canSave: kedua field non-blank + tidak sedang saving + sudah selesai loading.
 */
data class EditCardUiState(
    val isLoading: Boolean = true,
    val front: String = "",
    val back: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = front.isNotBlank() && back.isNotBlank() && !isSaving && !isLoading
}

/**
 * ViewModel untuk EditCardScreen.
 *
 * Saat init: load existing card via CardRepository.getCardById(),
 * pre-fill form fields.
 *
 * Saat saveCard: panggil CardRepository.updateCardContent() lalu trigger onSuccess.
 */
class EditCardViewModel(
    private val cardId: Long,
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCardUiState())
    val uiState: StateFlow<EditCardUiState> = _uiState.asStateFlow()

    init {
        loadCard()
    }

    private fun loadCard() {
        viewModelScope.launch {
            try {
                val card = cardRepository.getCardById(cardId)
                if (card == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Kartu tidak ditemukan (mungkin sudah dihapus)",
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            front = card.front,
                            back = card.back,
                            errorMessage = null,
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal load kartu",
                    )
                }
            }
        }
    }

    fun onFrontChange(value: String) {
        _uiState.update { it.copy(front = value, errorMessage = null) }
    }

    fun onBackChange(value: String) {
        _uiState.update { it.copy(back = value, errorMessage = null) }
    }

    fun saveCard(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (!current.canSave) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                cardRepository.updateCardContent(
                    id = cardId,
                    front = current.front.trim(),
                    back = current.back.trim(),
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Gagal simpan perubahan",
                    )
                }
            }
        }
    }
}