package com.example.Roomie.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

class AdminViewModel(
    private val reportRepository: ReportRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        observeAdminData()
    }

    private fun observeAdminData() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            reportRepository.getAllReports().collectLatest { reports ->
                _uiState.value = AdminUiState.Success(
                    allReports = reports.reversed(),
                    pendingCount = reports.count { it.status == ReportStatus.PENDING },
                    highUrgencyCount = reports.count { it.urgency == UrgencyLevel.HIGH }
                )
            }
        }
    }

    fun updateReportStatus(reportId: String, newStatus: ReportStatus) {
        viewModelScope.launch {
            reportRepository.updateReportStatus(reportId, newStatus)
        }
    }
}
