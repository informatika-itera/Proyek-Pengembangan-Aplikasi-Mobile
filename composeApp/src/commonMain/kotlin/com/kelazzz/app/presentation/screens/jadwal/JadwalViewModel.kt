package com.kelazzz.app.presentation.screens.jadwal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.domain.repository.JadwalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class JadwalViewModel(
    private val repository: JadwalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JadwalUiState())
    val uiState: StateFlow<JadwalUiState> = _uiState.asStateFlow()

    init {
        loadJadwal()
    }

    private fun loadJadwal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAllJadwal()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { list ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            jadwalList = list,
                            error = null
                        )
                    }
                }
        }
    }

    // ==================== FORM STATE ====================

    fun showAddDialog() {
        _uiState.update {
            it.copy(
                showFormDialog = true,
                editingJadwal = null,
                formJudul = "",
                formDeskripsi = "",
                formTanggal = "",
                formWaktu = "",
                formJenis = JenisJadwal.REMINDER,
                formError = null
            )
        }
    }

    fun showEditDialog(jadwal: Jadwal) {
        _uiState.update {
            it.copy(
                showFormDialog = true,
                editingJadwal = jadwal,
                formJudul = jadwal.judul,
                formDeskripsi = jadwal.deskripsi,
                formTanggal = jadwal.tanggal,
                formWaktu = jadwal.waktu,
                formJenis = jadwal.jenis,
                formError = null
            )
        }
    }

    fun dismissFormDialog() {
        _uiState.update { it.copy(showFormDialog = false) }
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

    // ==================== CRUD ====================

    fun saveJadwal() {
        val state = _uiState.value

        if (state.formJudul.isBlank()) {
            _uiState.update { it.copy(formError = "Nama mata kuliah harus diisi") }
            return
        }
        if (state.formTanggal.isBlank()) {
            _uiState.update { it.copy(formError = "Hari harus diisi") }
            return
        }

        viewModelScope.launch {
            val now = Clock.System.now()
            val jadwal = Jadwal(
                id = state.editingJadwal?.id ?: 0,
                judul = state.formJudul.trim(),
                deskripsi = state.formDeskripsi.trim(),
                tanggal = state.formTanggal.trim(),
                waktu = state.formWaktu.trim(),
                jenis = state.formJenis,
                createdAt = state.editingJadwal?.createdAt ?: now,
                updatedAt = now
            )

            if (state.editingJadwal != null) {
                repository.updateJadwal(jadwal)
            } else {
                repository.insertJadwal(jadwal)
            }

            _uiState.update { it.copy(showFormDialog = false) }
        }
    }

    fun showDeleteConfirm(jadwal: Jadwal) {
        _uiState.update { it.copy(deletingJadwal = jadwal) }
    }

    fun dismissDeleteConfirm() {
        _uiState.update { it.copy(deletingJadwal = null) }
    }

    fun confirmDelete() {
        val jadwal = _uiState.value.deletingJadwal ?: return
        viewModelScope.launch {
            repository.deleteJadwal(jadwal.id)
            _uiState.update { it.copy(deletingJadwal = null) }
        }
    }
}

data class JadwalUiState(
    val isLoading: Boolean = true,
    val jadwalList: List<Jadwal> = emptyList(),
    val error: String? = null,

    // Form dialog state
    val showFormDialog: Boolean = false,
    val editingJadwal: Jadwal? = null,
    val formJudul: String = "",
    val formDeskripsi: String = "",
    val formTanggal: String = "",
    val formWaktu: String = "",
    val formJenis: JenisJadwal = JenisJadwal.REMINDER,
    val formError: String? = null,

    // Delete dialog
    val deletingJadwal: Jadwal? = null
) {
    val isEditing: Boolean get() = editingJadwal != null
}
