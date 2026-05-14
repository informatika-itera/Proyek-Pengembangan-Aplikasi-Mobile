package com.example.Roomie.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val reports: List<Report>,
        val stats: Map<ReportStatus, Int>
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                // Simulasi data fetch (nantinya dari Repository)
                kotlinx.coroutines.delay(1000)
                val dummyReports = listOf(
                    Report(
                        id = "1",
                        category = "Gedung Kuliah",
                        location = "Gedung E 203",
                        description = "AC Mati dan bocor",
                        urgency = UrgencyLevel.MEDIUM,
                        status = ReportStatus.DONE,
                        createdAt = 1715673600000
                    ),
                    Report(
                        id = "2",
                        category = "Lab",
                        location = "Lab Multimedia",
                        description = "PC No. 12 tidak bisa menyala",
                        urgency = UrgencyLevel.HIGH,
                        status = ReportStatus.IN_PROGRESS,
                        createdAt = 1715760000000
                    )
                )
                
                val stats = mapOf(
                    ReportStatus.PENDING to 0,
                    ReportStatus.IN_PROGRESS to 1,
                    ReportStatus.DONE to 1
                )
                
                _uiState.value = ProfileUiState.Success(dummyReports, stats)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Terjadi kesalahan memuat profil")
            }
        }
    }
}
