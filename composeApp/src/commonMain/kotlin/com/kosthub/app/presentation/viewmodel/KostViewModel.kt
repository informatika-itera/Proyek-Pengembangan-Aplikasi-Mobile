package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.data.DummyKostData
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.domain.repository.KostRepository
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KostViewModel(
    private val repository: KostRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _uiState = MutableStateFlow<UiState<List<Kost>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Kost>>> = _uiState

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    init {
        scope.launch {
            try {
                repository.seedIfEmpty(DummyKostData.seedData())
                refresh()
            } catch (error: Exception) {
                _uiState.value = UiState.Error(error.message ?: "Gagal memuat data")
            }
        }
    }

    fun refresh() {
        scope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = repository.getAll()
                _uiState.value = if (data.isEmpty()) UiState.Empty else UiState.Success(data)
            } catch (error: Exception) {
                _uiState.value = UiState.Error(error.message ?: "Gagal memuat data")
            }
        }
    }

    fun addKost(kost: Kost) {
        scope.launch {
            runOperation(
                successMessage = "Kost berhasil ditambahkan"
            ) {
                repository.add(kost)
            }
        }
    }

    fun updateKost(kost: Kost) {
        scope.launch {
            runOperation(
                successMessage = "Kost berhasil diperbarui"
            ) {
                repository.update(kost)
            }
        }
    }

    fun deleteKost(id: Long) {
        scope.launch {
            runOperation(
                successMessage = "Kost berhasil dihapus"
            ) {
                repository.delete(id)
            }
        }
    }

    fun toggleFavorite(kost: Kost) {
        scope.launch {
            runOperation(
                successMessage = if (kost.isFavorite) "Kost dihapus dari favorit" else "Kost ditambahkan ke favorit"
            ) {
                repository.update(kost.copy(isFavorite = !kost.isFavorite))
            }
        }
    }

    fun clearOperationState() {
        _operationState.value = OperationState.Idle
    }

    fun dispose() {
        scope.cancel()
    }

    private suspend fun runOperation(
        successMessage: String,
        action: suspend () -> Unit
    ) {
        _operationState.value = OperationState.Loading
        try {
            action()
            refresh()
            _operationState.value = OperationState.Success(successMessage)
        } catch (error: Exception) {
            _operationState.value = OperationState.Error(error.message ?: "Operasi gagal")
        }
    }
}
