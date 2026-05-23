package com.kelazzz.app.presentation.screens.rekap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Kelas
import com.kelazzz.app.domain.model.Presensi
import com.kelazzz.app.domain.repository.PresensiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Model UI untuk menggabungkan Kelas dengan data ringkasan absensi (jika ada)
 */
data class KelasUiModel(
    val kelas: Kelas,
    val summary: AttendanceSummary?
)

/**
 * UI State untuk halaman Rekap Presensi
 */
data class RekapUiState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val kelasList: List<KelasUiModel> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val syncSuccess: Boolean = false,
    val selectedKelas: Kelas? = null,
    val isDetailLoading: Boolean = false,
    val presensiDetailList: List<Presensi> = emptyList()
) {
    val filteredKelasList: List<KelasUiModel>
        get() = if (searchQuery.isBlank()) {
            kelasList
        } else {
            kelasList.filter {
                it.kelas.namaMk.contains(searchQuery, ignoreCase = true) ||
                        it.kelas.kodeMk.contains(searchQuery, ignoreCase = true) ||
                        it.kelas.namaDosenList.contains(searchQuery, ignoreCase = true)
            }
        }
}

class RekapViewModel(
    private val repository: PresensiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RekapUiState())
    val uiState: StateFlow<RekapUiState> = _uiState.asStateFlow()

    init {
        loadKelas()
        syncKelas()
    }

    /**
     * Memuat daftar kelas dan menggabungkannya dengan ringkasan presensi dari database lokal
     */
    fun loadKelas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                repository.getKelasList(),
                repository.getAttendanceSummary()
            ) { kelasList, summaryList ->
                kelasList.map { kelas ->
                    val summary = summaryList.find { it.mataKuliahId == kelas.kodeKelas }
                    KelasUiModel(kelas, summary)
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        kelasList = list,
                        error = null
                    )
                }
            }
        }
    }

    /**
     * Melakukan sinkronisasi daftar kelas terbaru dari API ke database lokal
     */
    fun syncKelas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncSuccess = false) }
            val result = repository.syncKelas()
            if (result.isSuccess) {
                _uiState.update { it.copy(isSyncing = false, syncSuccess = true, error = null) }
            } else {
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        error = result.exceptionOrNull()?.message ?: "Gagal memperbarui kelas."
                    )
                }
            }
        }
    }

    /**
     * Mengubah kata kunci pencarian mata kuliah
     */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Membersihkan pesan error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Reset flag sukses sinkronisasi
     */
    fun clearSyncSuccess() {
        _uiState.update { it.copy(syncSuccess = false) }
    }

    private var detailJob: kotlinx.coroutines.Job? = null

    /**
     * Memilih mata kuliah untuk menampilkan detail presensi pertemuan
     */
    fun selectKelas(kelas: Kelas) {
        _uiState.update { 
            it.copy(
                selectedKelas = kelas, 
                isDetailLoading = true
            ) 
        }
        
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            // 1. Observe data lokal
            launch {
                repository.getPresensiByMataKuliah(kelas.kodeKelas)
                    .collect { list ->
                        _uiState.update { 
                            it.copy(
                                presensiDetailList = list,
                                isDetailLoading = list.isEmpty()
                            ) 
                        }
                    }
            }
            
            // 2. Trigger sync background dari API
            launch {
                val result = repository.syncPresensiForKelas(kelas.kodeKelas, kelas.namaMk)
                _uiState.update { it.copy(isDetailLoading = false) }
                if (result.isFailure) {
                    _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
                }
            }
        }
    }

    /**
     * Menutup sheet detail mata kuliah
     */
    fun dismissKelasDetail() {
        detailJob?.cancel()
        _uiState.update { 
            it.copy(
                selectedKelas = null, 
                presensiDetailList = emptyList(),
                isDetailLoading = false
            ) 
        }
    }
}
