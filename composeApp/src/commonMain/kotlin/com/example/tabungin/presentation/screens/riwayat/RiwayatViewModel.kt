package com.example.tabungin.presentation.screens.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.usecase.GetAllSetoranUseCase
import kotlinx.coroutines.flow.*

data class RiwayatUiState(
    val setoranList: List<Setoran> = emptyList(),
    val isLoading: Boolean         = true,
    val totalSetoran: Double       = 0.0
)

class RiwayatViewModel(
    private val getAllSetoranUseCase: GetAllSetoranUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RiwayatUiState())
    val uiState: StateFlow<RiwayatUiState> = _uiState.asStateFlow()

    init {
        getAllSetoranUseCase()
            .onEach { list ->
                _uiState.update {
                    it.copy(
                        setoranList  = list,
                        isLoading    = false,
                        totalSetoran = list.sumOf { s -> s.amount }
                    )
                }
            }
            .catch { _uiState.update { it.copy(isLoading = false) } }
            .launchIn(viewModelScope)
    }
}