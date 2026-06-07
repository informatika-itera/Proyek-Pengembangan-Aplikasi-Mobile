package com.kelazzz.app.presentation.screens.jadwal.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.presentation.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class JadwalDetailViewModel(
    private val repository: JadwalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val jadwalId: Long = savedStateHandle.get<Long>("id") 
        ?: throw IllegalArgumentException("ID Jadwal tidak boleh kosong")

    private val _uiState = MutableStateFlow<JadwalDetailUiState>(JadwalDetailUiState.Loading)
    val uiState: StateFlow<JadwalDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = JadwalDetailUiState.Loading
            repository.getJadwalById(jadwalId)
                .catch { e ->
                    _uiState.value = JadwalDetailUiState.Error(e.message ?: "Terjadi kesalahan")
                }
                .collect { jadwal ->
                    _uiState.value = if (jadwal != null) {
                        JadwalDetailUiState.Success(jadwal)
                    } else {
                        JadwalDetailUiState.Error("Jadwal tidak ditemukan")
                    }
                }
        }
    }

    fun deleteJadwal(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteJadwal(jadwalId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = JadwalDetailUiState.Error(e.message ?: "Gagal menghapus jadwal")
            }
        }
    }
}

sealed interface JadwalDetailUiState {
    data object Loading : JadwalDetailUiState
    data class Success(val jadwal: Jadwal) : JadwalDetailUiState
    data class Error(val message: String) : JadwalDetailUiState
}
