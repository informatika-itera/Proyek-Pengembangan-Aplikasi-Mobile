package com.example.Roomie.presentation.admin

import com.example.Roomie.data.repository.*
import com.example.Roomie.domain.model.*
import com.example.Roomie.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var reportRepository: FakeReportRepository
    private lateinit var facilityRepository: FakeFacilityRepository
    private lateinit var bookingRepository: FakeBookingRepository
    private lateinit var announcementRepository: FakeAnnouncementRepository
    
    private lateinit var viewModel: AdminViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        reportRepository = FakeReportRepository()
        facilityRepository = FakeFacilityRepository()
        bookingRepository = FakeBookingRepository()
        announcementRepository = FakeAnnouncementRepository()
        
        val notificationRepository = object : com.example.Roomie.domain.repository.NotificationRepository {
            override fun getAllNotifications() = flowOf(emptyList<Notification>())
            override suspend fun addNotification(notification: Notification) {}
            override suspend fun markAsRead(id: String) {}
        }
        
        val auditRepository = object : com.example.Roomie.domain.repository.AuditRepository {
            private val logs = mutableListOf<AuditLog>()
            override fun getAuditLogs() = flowOf(logs)
            override suspend fun addAuditLog(log: AuditLog) = Result.success(Unit).also { logs.add(log) }
        }

        viewModel = AdminViewModel(
            getAllReportsUseCase = GetAllReportsUseCase(reportRepository),
            updateReportStatusUseCase = UpdateReportStatusUseCase(reportRepository),
            updateRoomStatusUseCase = UpdateRoomStatusUseCase(facilityRepository),
            postAnnouncementUseCase = PostAnnouncementUseCase(announcementRepository),
            getBuildingsUseCase = GetBuildingsUseCase(facilityRepository),
            searchRoomsUseCase = SearchRoomsUseCase(facilityRepository),
            getAllBookingsUseCase = GetAllBookingsUseCase(bookingRepository),
            getAuditLogsUseCase = GetAuditLogsUseCase(auditRepository),
            addAuditLogUseCase = AddAuditLogUseCase(auditRepository),
            bookingRepository = bookingRepository,
            notificationRepository = notificationRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading then Success`() = runTest {
        assertTrue(viewModel.uiState.value is AdminUiState.Loading)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is AdminUiState.Success)
    }

    @Test
    fun `approveBooking should update status and add audit log`() = runTest {
        val booking = Booking("B1", "R1", "101", "GKU2", 0L, 0L, BookingStatus.PENDING, "Test")
        bookingRepository.setBookings(listOf(booking))
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.approveBooking(booking)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val updatedBooking = bookingRepository.getAllBookings().first().first()
        assertEquals(BookingStatus.APPROVED, updatedBooking.status)
    }

    @Test
    fun `onReportSearch should filter reports`() = runTest {
        val reports = listOf(
            Report("1", "A", "Loc A", "Broken AC", UrgencyLevel.HIGH, ReportStatus.PENDING, 0L),
            Report("2", "B", "Loc B", "Leak", UrgencyLevel.LOW, ReportStatus.PENDING, 0L)
        )
        reportRepository.setReports(reports)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onReportSearch("AC")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value as AdminUiState.Success
        assertEquals(1, state.filteredReports.size)
        assertTrue(state.filteredReports[0].description.contains("AC"))
    }
}
