package com.example.tabungin.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.flow.*

data class DetailUiState(
    val target: Target?          = null,
    val setoranList: List<Setoran> = emptyList(),
    val isLoading: Boolean       = true,
    val error: String?           = null,
    val showSetoranDialog: Boolean = false
)

class DetailViewModel(
    private val targetId: Long,
    private val getTargetByIdUseCase: GetTargetByIdUseCase,
    private val getSetoranByTargetUseCase: GetSetoranByTargetUseCase,
    private val insertSetoranUseCase: InsertSetoranUseCase,
    private val deleteSetoranUseCase: DeleteSetoranUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init { observeData() }

    private fun observeData() {
        combine(
            getTargetByIdUseCase(targetId),
            getSetoranByTargetUseCase(targetId)
        ) { target, setoran -> target to setoran }
            .onEach { (target, setoran) ->
                _uiState.update {
                    it.copy(target = target, setoranList = setoran, isLoading = false)
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    fun tambahSetoran(amount: Double, catatan: String) {
        viewModelScope.launch {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toString()
            runCatching {
                insertSetoranUseCase(
                    Setoran(targetId = targetId, amount = amount, catatan = catatan, tanggal = today)
                )
            }.onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun deleteSetoran(id: Long) {
        viewModelScope.launch {
            runCatching { deleteSetoranUseCase(id) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun showSetoranDialog()    = _uiState.update { it.copy(showSetoranDialog = true) }
    fun dismissSetoranDialog() = _uiState.update { it.copy(showSetoranDialog = false) }
    fun clearError()           = _uiState.update { it.copy(error = null) }
}
