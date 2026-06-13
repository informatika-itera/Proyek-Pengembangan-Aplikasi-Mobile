package com.example.Roomie.presentation.admin

import com.example.Roomie.data.repository.*
import com.example.Roomie.domain.model.*
import com.example.Roomie.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var reportRepository: FakeReportRepository
    private lateinit var facilityRepository: FakeFacilityRepository
    private lateinit var bookingRepository: FakeBookingRepository
    private lateinit var announcementRepository: FakeAnnouncementRepository
    private val mockAuditRepo = mockk<com.example.Roomie.domain.repository.AuditRepository>(relaxed = true)
    
    private lateinit var viewModel: AdminViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        reportRepository = FakeReportRepository()
        facilityRepository = FakeFacilityRepository()
        bookingRepository = FakeBookingRepository()
        announcementRepository = FakeAnnouncementRepository()
        
        every { mockAuditRepo.getAuditLogs() } returns flowOf(emptyList())

        viewModel = AdminViewModel(
            getAllReportsUseCase = GetAllReportsUseCase(reportRepository),
            updateReportStatusUseCase = UpdateReportStatusUseCase(reportRepository),
            updateRoomStatusUseCase = UpdateRoomStatusUseCase(facilityRepository),
            postAnnouncementUseCase = PostAnnouncementUseCase(announcementRepository),
            getBuildingsUseCase = GetBuildingsUseCase(facilityRepository),
            searchRoomsUseCase = SearchRoomsUseCase(facilityRepository),
            getAllBookingsUseCase = GetAllBookingsUseCase(bookingRepository),
            getAuditLogsUseCase = GetAuditLogsUseCase(mockAuditRepo),
            addAuditLogUseCase = AddAuditLogUseCase(mockAuditRepo),
            bookingRepository = bookingRepository,
            notificationRepository = mockk(relaxed = true)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization and filter coverage`() = runTest {
        assertTrue(viewModel.uiState.value is AdminUiState.Success)
        
        viewModel.onReportSearch("Test")
        viewModel.onBookingSearch("101")
        viewModel.onReportStatusFilter(ReportStatus.PENDING)
        
        val state = viewModel.uiState.value as AdminUiState.Success
        assertEquals("Test", state.filter.reportQuery)
    }

    @Test
    fun `admin actions coverage - approve and override`() = runTest {
        val booking = Booking("B1", "R1", "101", "GKU2", 0L, 0L, BookingStatus.PENDING, "Test")
        bookingRepository.setBookings(listOf(booking))
        
        viewModel.approveBooking(booking)
        viewModel.overrideRoomStatus("R1", RoomStatus.MAINTENANCE, "Broken")
        
        coVerify { mockAuditRepo.addAuditLog(any()) }
    }
}
