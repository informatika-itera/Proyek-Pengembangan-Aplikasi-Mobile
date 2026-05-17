package com.example.Roomie.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.*
import com.example.Roomie.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed interface AdminUiState {
    data object Loading : AdminUiState
    data class Success(
        val allReports: List<Report>,
        val buildings: List<Building>,
        val rooms: List<Room>,
        val pendingCount: Int,
        val highUrgencyCount: Int
    ) : AdminUiState
    data class Error(val message: String) : AdminUiState
}

class AdminViewModel(
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val updateReportStatusUseCase: UpdateReportStatusUseCase,
    private val updateRoomStatusUseCase: UpdateRoomStatusUseCase,
    private val postAnnouncementUseCase: PostAnnouncementUseCase,
    private val getBuildingsUseCase: GetBuildingsUseCase,
    private val getRoomsByBuildingUseCase: GetRoomsByBuildingUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        observeAllData()
    }

    private fun observeAllData() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            
            combine(
                getAllReportsUseCase(),
                getBuildingsUseCase(),
                // Kita ambil data GKU2 sebagai default untuk picker ruangan
                getRoomsByBuildingUseCase("GKU2")
            ) { reports, buildings, rooms ->
                AdminUiState.Success(
                    allReports = reports.reversed(),
                    buildings = buildings,
                    rooms = rooms,
                    pendingCount = reports.count { it.status == ReportStatus.PENDING },
                    highUrgencyCount = reports.count { it.urgency == UrgencyLevel.HIGH }
                )
            }.catch { e ->
                _uiState.value = AdminUiState.Error(e.message ?: "Gagal memuat data admin")
            }.collect { state ->
                _uiState.value = state
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
