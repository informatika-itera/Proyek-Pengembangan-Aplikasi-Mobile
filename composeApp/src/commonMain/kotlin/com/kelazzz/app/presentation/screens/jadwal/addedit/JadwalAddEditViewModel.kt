package com.kelazzz.app.presentation.screens.jadwal.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.presentation.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class JadwalAddEditViewModel(
    private val repository: JadwalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val jadwalId: Long? = savedStateHandle.get<Long>("id")

    private val _uiState = MutableStateFlow(JadwalAddEditUiState())
    val uiState: StateFlow<JadwalAddEditUiState> = _uiState.asStateFlow()

    init {
        loadJadwalForEdit()
    }

    private fun loadJadwalForEdit() {
        if (jadwalId == null) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getJadwalById(jadwalId)
                .collect { jadwal ->
                    if (jadwal != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                formJudul = jadwal.judul,
                                formDeskripsi = jadwal.deskripsi,
                                formTanggal = jadwal.tanggal,
                                formWaktu = jadwal.waktu,
                                formJenis = jadwal.jenis
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, formError = "Jadwal tidak ditemukan") }
                    }
                }
        }
    }

    fun onJudulChange(value: String) {
        _uiState.update { it.copy(formJudul = value, formError = null) }
    }

    fun onDeskripsiChange(value: String) {
        _uiState.update { it.copy(formDeskripsi = value) }
    }

    fun onTanggalChange(value: String) {
        _uiState.update { it.copy(formTanggal = value, formError = null) }
    }

    fun onWaktuChange(value: String) {
        _uiState.update { it.copy(formWaktu = value) }
    }

    fun onJenisChange(value: JenisJadwal) {
        _uiState.update { it.copy(formJenis = value) }
    }

    fun saveJadwal(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.formJudul.isBlank()) {
            _uiState.update { it.copy(formError = "Nama mata kuliah / judul harus diisi") }
            return
        }
        if (state.formTanggal.isBlank()) {
            _uiState.update { it.copy(formError = "Hari harus diisi") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val now = Clock.System.now()
                val jadwal = Jadwal(
                    id = jadwalId ?: 0,
                    judul = state.formJudul.trim(),
                    deskripsi = state.formDeskripsi.trim(),
                    tanggal = state.formTanggal.trim(),
                    waktu = state.formWaktu.trim(),
                    jenis = state.formJenis,
                    createdAt = now, // will be ignored by repository update queries
                    updatedAt = now
                )

                if (jadwalId != null) {
                    repository.updateJadwal(jadwal)
                } else {
                    repository.insertJadwal(jadwal)
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, formError = e.message ?: "Gagal menyimpan jadwal") }
            }
        }
    }
}

data class JadwalAddEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val formJudul: String = "",
    val formDeskripsi: String = "",
    val formTanggal: String = "",
    val formWaktu: String = "",
    val formJenis: JenisJadwal = JenisJadwal.REMINDER,
    val formError: String? = null
)
