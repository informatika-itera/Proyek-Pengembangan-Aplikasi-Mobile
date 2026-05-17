package com.example.tabungin.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.DeleteTargetUseCase
import com.example.tabungin.domain.usecase.GetAllTargetsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

data class HomeUiState(
    val targets: List<Target> = emptyList(),
    val isLoading: Boolean    = true,
    val error: String?        = null,
    val totalTerkumpul: Double = 0.0,
    val totalTarget: Double   = 0.0
)

class HomeViewModel(
    private val getAllTargetsUseCase: GetAllTargetsUseCase,
    private val deleteTargetUseCase: DeleteTargetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { observeTargets() }

    private fun observeTargets() {
        getAllTargetsUseCase()
            .onEach { list ->
                _uiState.update {
                    it.copy(
                        targets        = list,
                        isLoading      = false,
                        totalTerkumpul = list.sumOf { t -> t.terkumpul },
                        totalTarget    = list.sumOf { t -> t.targetAmount }
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    fun deleteTarget(id: Long) {
        viewModelScope.launch {
            runCatching { deleteTargetUseCase(id) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
