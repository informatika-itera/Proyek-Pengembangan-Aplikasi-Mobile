package com.kelazzz.app.presentation.screens.presensi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.repository.PresensiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State UI untuk halaman presensi.
 */
sealed class PresensiUiState {
    /** Menunggu pemindaian QR Code atau input token manual */
    data object Scanning : PresensiUiState()

    /** Token terdeteksi, sedang dikirim ke server ITERA */
    data object Loading : PresensiUiState()

    /** Presensi berhasil dicatat */
    data class Success(val message: String = "Presensi berhasil dicatat!") : PresensiUiState()

    /** Presensi gagal */
    data class Error(val message: String) : PresensiUiState()
}

/**
 * ViewModel untuk mengelola status presensi dan interaksi dengan repository.
 *
 * Alur kerja:
 * 1. User scan QR / input token manual
 * 2. Token dikirim ke submitPresensi (API: POST /v2/presensi/kelas)
 * 3. Hasilnya ditampilkan sebagai Success / Error
 */
class PresensiViewModel(
    private val presensiRepository: PresensiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PresensiUiState>(PresensiUiState.Scanning)
    val uiState: StateFlow<PresensiUiState> = _uiState.asStateFlow()

    /**
     * Submit token presensi ke API ITERA.
     * Dipanggil baik dari hasil scan QR maupun input manual.
     *
     * @param token Token presensi dari QR Code (contoh: "35906-180984-a1c6db")
     */
    fun submitPresensi(token: String) {
        // Validasi token tidak kosong
        if (token.isBlank()) {
            _uiState.value = PresensiUiState.Error("Token presensi tidak boleh kosong.")
            return
        }

        _uiState.value = PresensiUiState.Loading

        viewModelScope.launch {
            val result = presensiRepository.submitPresensi(token.trim())
            _uiState.value = if (result.isSuccess) {
                PresensiUiState.Success()
            } else {
                PresensiUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal melakukan presensi."
                )
            }
        }
    }

    /**
     * Reset state kembali ke mode scanning (untuk scan ulang / coba lagi).
     */
    fun resetToScanning() {
        _uiState.value = PresensiUiState.Scanning
    }
}
