package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.data.DummyKostData
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.domain.repository.KostRepository
import com.kosthub.app.presentation.state.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

class KostViewModel(
    private val repository: KostRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _uiState = MutableStateFlow<UiState<List<Kost>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Kost>>> = _uiState

    init {
        scope.launch {
            repository.seedIfEmpty(DummyKostData.seedData())
            refresh()
        }
    }

    fun refresh() {
        scope.launch {
            _uiState.value = UiState.Loading
            val data = repository.getAll()
            _uiState.value = if (data.isEmpty()) UiState.Empty else UiState.Success(data)
        }
    }

    fun addKost() {
        scope.launch {
            val currentSize = (uiState.value as? UiState.Success)?.data?.size ?: 0
            repository.add(DummyKostData.newItem(max(1, currentSize + 1)))
            refresh()
        }
    }

    fun updateKost(kost: Kost) {
        scope.launch {
            repository.update(kost.copy(namaKos = "${kost.namaKos} (Edited)"))
            refresh()
        }
    }

    fun deleteKost(id: Long) {
        scope.launch {
            repository.delete(id)
            refresh()
        }
    }

    fun toggleFavorite(kost: Kost) {
        scope.launch {
            repository.update(kost.copy(isFavorite = !kost.isFavorite))
            refresh()
        }
    }

    fun dispose() {
        scope.cancel()
    }
}
