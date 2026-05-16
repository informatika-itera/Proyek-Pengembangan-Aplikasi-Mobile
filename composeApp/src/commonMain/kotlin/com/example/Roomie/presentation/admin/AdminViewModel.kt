package com.example.Roomie.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Announcement
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.usecase.GetAllReportsUseCase
import com.example.Roomie.domain.usecase.UpdateReportStatusUseCase
import com.example.Roomie.domain.usecase.UpdateRoomStatusUseCase
import com.example.Roomie.domain.usecase.PostAnnouncementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

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
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val updateReportStatusUseCase: UpdateReportStatusUseCase,
    private val updateRoomStatusUseCase: UpdateRoomStatusUseCase,
    private val postAnnouncementUseCase: PostAnnouncementUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        observeAdminData()
    }

    private fun observeAdminData() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            getAllReportsUseCase().collectLatest { reports ->
                _uiState.value = AdminUiState.Success(
                    allReports = reports.reversed(),
                    pendingCount = reports.count { it.status == ReportStatus.PENDING },
                    highUrgencyCount = reports.count { it.urgency.name == "HIGH" }
                )
            }
        }
    }

    fun updateReportStatus(reportId: String, newStatus: ReportStatus) {
        viewModelScope.launch {
            updateReportStatusUseCase(reportId, newStatus)
        }
    }

    fun overrideRoomStatus(roomId: String, status: RoomStatus, note: String?) {
        viewModelScope.launch {
            updateRoomStatusUseCase(
                roomId = roomId,
                status = status,
                maintenanceDescription = if (status == RoomStatus.MAINTENANCE) note else null,
                borrowerName = if (status == RoomStatus.BOOKED) note else null
            )
        }
    }

    fun broadcastMessage(title: String, message: String) {
        viewModelScope.launch {
            val announcement = Announcement(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                title = title,
                message = message,
                author = "Admin Sarpras",
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            postAnnouncementUseCase(announcement)
        }
    }
}
