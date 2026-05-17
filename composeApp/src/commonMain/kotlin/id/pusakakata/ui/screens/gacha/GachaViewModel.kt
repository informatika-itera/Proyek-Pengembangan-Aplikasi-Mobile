package id.pusakakata.ui.screens.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.usecase.GachaSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GachaUiState {
    object Idle : GachaUiState()
    object Drawing : GachaUiState()
    data class Result(val card: LegendaryCard) : GachaUiState()
}

class GachaViewModel(private val gachaSystem: GachaSystem) : ViewModel() {
    private val _uiState = MutableStateFlow<GachaUiState>(GachaUiState.Idle)
    val uiState: StateFlow<GachaUiState> = _uiState

    fun drawCard() {
        viewModelScope.launch {
            _uiState.value = GachaUiState.Drawing
            // Simulate delay for effect
            kotlinx.coroutines.delay(1500)
            val card = gachaSystem.drawCard()
            _uiState.value = GachaUiState.Result(card)
        }
    }

    fun reset() {
        _uiState.value = GachaUiState.Idle
    }
}
