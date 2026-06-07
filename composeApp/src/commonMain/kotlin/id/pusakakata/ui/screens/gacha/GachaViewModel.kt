package id.pusakakata.ui.screens.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.usecase.GachaSystem
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

sealed class GachaUiState {
    object Idle : GachaUiState()
    object Drawing : GachaUiState()
    data class Result(val card: LegendaryCard) : GachaUiState()
    data class Error(val message: String) : GachaUiState()
}

class GachaViewModel(
    private val gachaSystem: GachaSystem,
    private val repository: ItemRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<GachaUiState>(GachaUiState.Idle)
    val uiState: StateFlow<GachaUiState> = _uiState

    val tokens: StateFlow<Long> = repository.getTokens()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun drawCard() {
        viewModelScope.launch {
            if (repository.useToken()) {
                _uiState.value = GachaUiState.Drawing
                delay(1500)
                val card = gachaSystem.drawCard()
                repository.saveCollectedCard(card.id) // Save to collection
                _uiState.value = GachaUiState.Result(card)
            } else {
                _uiState.value = GachaUiState.Error("Token tidak cukup! Selesaikan kuis untuk dapat token.")
            }
        }
    }

    fun reset() {
        _uiState.value = GachaUiState.Idle
    }
}
