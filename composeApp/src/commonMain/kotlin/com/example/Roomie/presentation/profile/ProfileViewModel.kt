package com.example.Roomie.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.remote.SupabaseService
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import com.example.Roomie.domain.usecase.GetAllReportsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val user: User?,
        val avatarUrl: String?,
        val reports: List<Report>,
        val stats: Map<ReportStatus, Int>,
        val isUploading: Boolean = false
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val userPreferences: UserPreferences,
    private val supabaseService: SupabaseService
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfileData()
    }

    fun observeProfileData() {
        viewModelScope.launch {
            combine(
                getCurrentUserUseCase(),
                getAllReportsUseCase(),
                userPreferences.userAvatar
            ) { user, reports, avatar ->
                val stats = mapOf(
                    ReportStatus.PENDING to reports.count { it.status == ReportStatus.PENDING },
                    ReportStatus.IN_PROGRESS to reports.count { it.status == ReportStatus.IN_PROGRESS },
                    ReportStatus.DONE to reports.count { it.status == ReportStatus.DONE }
                )
                ProfileUiState.Success(
                    user = user,
                    avatarUrl = avatar,
                    reports = reports.reversed(),
                    stats = stats
                )
            }.catch { e ->
                _uiState.value = ProfileUiState.Error(e.message ?: "Gagal memuat profil")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateAvatar(bytes: ByteArray) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ProfileUiState.Success) {
                _uiState.value = currentState.copy(isUploading = true)
                
                try {
                    val url = supabaseService.uploadAvatar(bytes)
                    if (url != null) {
                        userPreferences.saveAvatar(url)
                    } else {
                        // Jika URL null tapi tidak throw exception
                        _uiState.value = ProfileUiState.Error("Gagal mengupload gambar. Pastikan koneksi stabil.")
                        return@launch
                    }
                } catch (e: Exception) {
                    _uiState.value = ProfileUiState.Error("Gagal upload: ${e.message}")
                    return@launch
                }
                
                // Pastikan kita refresh state ke Success setelah upload (biasanya terpicu otomatis oleh collect flow)
                _uiState.value = currentState.copy(isUploading = false)
            }
        }
    }
}
