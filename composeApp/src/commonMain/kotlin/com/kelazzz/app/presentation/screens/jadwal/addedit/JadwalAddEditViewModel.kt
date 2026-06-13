package com.kelazzz.app.presentation.screens.jadwal.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.domain.model.ReminderOption
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.presentation.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val DAYS_OF_WEEK = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

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
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    formTanggal = "Senin"
                ) 
            }
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
                                formJenis = jadwal.jenis,
                                formReminderOption = ReminderOption.fromOffset(jadwal.reminderOffsetMinutes)
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
        _uiState.update { state ->
            val newTanggal = if (value == JenisJadwal.REMINDER) {
                if (state.formTanggal in DAYS_OF_WEEK) state.formTanggal else "Senin"
            } else {
                val isDateValid = runCatching { LocalDate.parse(state.formTanggal) }.isSuccess
                if (isDateValid) state.formTanggal else {
                    Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                        .toString()
                }
            }
            state.copy(
                formJenis = value,
                formTanggal = newTanggal
            )
        }
    }

    fun onReminderOptionChange(value: ReminderOption) {
        _uiState.update { it.copy(formReminderOption = value, formError = null) }
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
        if (state.formReminderOption != ReminderOption.NONE && !isValidSchedulableDateTime(state.formJenis, state.formTanggal, state.formWaktu)) {
            _uiState.update {
                it.copy(
                    formError = "Pilih tanggal dan waktu mulai agar notifikasi bisa dijadwalkan."
                )
            }
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
                    reminderOffsetMinutes = state.formReminderOption.offsetMinutes,
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

    private fun isValidSchedulableDateTime(jenis: JenisJadwal, tanggal: String, waktu: String): Boolean {
        val startTime = waktu.substringBefore("-").trim()
        val timeParts = startTime.split(":")
        if (timeParts.size != 2) return false
        val hour = timeParts[0].toIntOrNull() ?: return false
        val minute = timeParts[1].toIntOrNull() ?: return false
        val isTimeValid = hour in 0..23 && minute in 0..59
        if (!isTimeValid) return false

        return if (jenis == JenisJadwal.REMINDER) {
            tanggal.trim() in DAYS_OF_WEEK
        } else {
            val date = runCatching { LocalDate.parse(tanggal.trim()) }.getOrNull() ?: return false
            date.year in 2000..2100
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
    val formReminderOption: ReminderOption = ReminderOption.NONE,
    val formError: String? = null
)
