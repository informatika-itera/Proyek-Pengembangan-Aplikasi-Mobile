package com.example.neurodeck.presentation.screens.addcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddCardUiState(
    val front: String = "",
    val back: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = front.isNotBlank() && back.isNotBlank() && !isSaving
}

class AddCardViewModel(
    private val deckId: Long,
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCardUiState())
    val uiState: StateFlow<AddCardUiState> = _uiState.asStateFlow()

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
                cardRepository.createCard(
                    deckId = deckId,
                    front = current.front.trim(),
                    back = current.back.trim(),
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Gagal simpan kartu",
                    )
                }
            }
        }
    }
}
