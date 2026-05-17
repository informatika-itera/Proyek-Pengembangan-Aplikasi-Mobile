package com.example.Roomie.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.*
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.domain.repository.NotificationRepository
import com.example.Roomie.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class AdminFilterState(
    val reportQuery: String = "",
    val reportStatusFilter: ReportStatus? = null,
    val bookingQuery: String = "",
    val bookingStatusFilter: BookingStatus? = null
)

sealed interface AdminUiState {
    data object Loading : AdminUiState
    data class Success(
        val allReports: List<Report>,
        val filteredReports: List<Report>,
        val allBookings: List<Booking>,
        val filteredBookings: List<Booking>,
        val buildings: List<Building>,
        val rooms: List<Room>,
        val pendingCount: Int,
        val highUrgencyCount: Int,
        val filter: AdminFilterState = AdminFilterState()
    ) : AdminUiState
    data class Error(val message: String) : AdminUiState
}

class AdminViewModel(
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val updateReportStatusUseCase: UpdateReportStatusUseCase,
    private val updateRoomStatusUseCase: UpdateRoomStatusUseCase,
    private val postAnnouncementUseCase: PostAnnouncementUseCase,
    private val getBuildingsUseCase: GetBuildingsUseCase,
    private val getRoomsByBuildingUseCase: GetRoomsByBuildingUseCase,
    private val getAllBookingsUseCase: GetAllBookingsUseCase,
    private val bookingRepository: BookingRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    
    private val _filterState = MutableStateFlow(AdminFilterState())
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
                getRoomsByBuildingUseCase("GKU2"),
                getAllBookingsUseCase(),
                _filterState
            ) { reports, buildings, rooms, bookings, filter ->
                
                val filteredReports = reports.filter { 
                    (filter.reportStatusFilter == null || it.status == filter.reportStatusFilter) &&
                    (filter.reportQuery.isEmpty() || it.description.contains(filter.reportQuery, ignoreCase = true) || it.location.contains(filter.reportQuery, ignoreCase = true))
                }.reversed()

                val filteredBookings = bookings.filter {
                    (filter.bookingStatusFilter == null || it.status == filter.bookingStatusFilter) &&
                    (filter.bookingQuery.isEmpty() || it.roomName.contains(filter.bookingQuery, ignoreCase = true) || it.subject?.contains(filter.bookingQuery, ignoreCase = true) == true)
                }.reversed()

                AdminUiState.Success(
                    allReports = reports,
                    filteredReports = filteredReports,
                    allBookings = bookings,
                    filteredBookings = filteredBookings,
                    buildings = buildings,
                    rooms = rooms,
                    pendingCount = reports.count { it.status == ReportStatus.PENDING },
                    highUrgencyCount = reports.count { it.urgency == UrgencyLevel.HIGH },
                    filter = filter
                )
            }.catch { e ->
                _uiState.value = AdminUiState.Error(e.message ?: "Gagal memuat data admin")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onReportSearch(query: String) {
        _filterState.update { it.copy(reportQuery = query) }
    }

    fun onReportStatusFilter(status: ReportStatus?) {
        _filterState.update { it.copy(reportStatusFilter = status) }
    }

    fun onBookingSearch(query: String) {
        _filterState.update { it.copy(bookingQuery = query) }
    }

    fun updateReportStatus(reportId: String, newStatus: ReportStatus) {
        viewModelScope.launch {
            updateReportStatusUseCase(reportId, newStatus)
        }
    }

    fun approveBooking(booking: Booking) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(booking.id, BookingStatus.APPROVED)
            updateRoomStatusUseCase(
                roomId = booking.roomId,
                status = RoomStatus.BOOKED,
                borrowerName = booking.subject ?: "Peminjaman Mahasiswa"
            )
            notificationRepository.addNotification(
                Notification(
                    id = Clock.System.now().toEpochMilliseconds().toString(),
                    title = "Peminjaman Disetujui!",
                    message = "Peminjaman ruangan ${booking.roomName} untuk '${booking.subject}' telah disetujui.",
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun rejectBooking(booking: Booking) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(booking.id, BookingStatus.REJECTED)
            notificationRepository.addNotification(
                Notification(
                    id = Clock.System.now().toEpochMilliseconds().toString(),
                    title = "Peminjaman Ditolak",
                    message = "Maaf, peminjaman ruangan ${booking.roomName} ditolak oleh Admin.",
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )
            )
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
