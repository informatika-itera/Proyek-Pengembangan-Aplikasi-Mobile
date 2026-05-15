package com.example.Roomie.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AdminUiState {
    data object Loading : AdminUiState
    data class Success(
        val allReports: List<Report>,
        val pendingCount: Int,
        val highUrgencyCount: Int
    ) : AdminUiState
    data class Error(val message: String) : AdminUiState
}

class AdminViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadAdminData()
    }

    private fun loadAdminData() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            delay(1000)
            try {
                val dummyReports = listOf(
                    Report("1", "Gedung Kuliah", "GKU 2 - 101", "AC Mati", UrgencyLevel.MEDIUM, ReportStatus.IN_PROGRESS, 1715673600000),
                    Report("2", "Fasilitas Umum", "Kantin", "Keran bocor", UrgencyLevel.LOW, ReportStatus.PENDING, 1715760000000),
                    Report("3", "Lab", "Lab Multimedia", "Proyektor Rusak", UrgencyLevel.HIGH, ReportStatus.PENDING, 1715846400000)
                )
                
                _uiState.value = AdminUiState.Success(
                    allReports = dummyReports,
                    pendingCount = dummyReports.count { it.status == ReportStatus.PENDING },
                    highUrgencyCount = dummyReports.count { it.urgency == UrgencyLevel.HIGH }
                )
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error("Gagal memuat data admin")
            }
        }
    }

    fun updateReportStatus(reportId: String, newStatus: ReportStatus) {
        val currentState = _uiState.value
        if (currentState is AdminUiState.Success) {
            val updatedReports = currentState.allReports.map {
                if (it.id == reportId) it.copy(status = newStatus) else it
            }
            _uiState.update { 
                currentState.copy(
                    allReports = updatedReports,
                    pendingCount = updatedReports.count { it.status == ReportStatus.PENDING }
                ) 
            }
        }
    }
}
