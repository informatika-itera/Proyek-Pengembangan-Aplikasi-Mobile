package com.example.Roomie.presentation.home

import com.example.Roomie.data.repository.FakeAnnouncementRepository
import com.example.Roomie.data.repository.FakeAuthRepository
import com.example.Roomie.data.repository.FakeReportRepository
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.usecase.GetAllAnnouncementsUseCase
import com.example.Roomie.domain.usecase.GetAllReportsUseCase
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class HomeViewModelTest {
    private lateinit var reportRepository: FakeReportRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var announcementRepository: FakeAnnouncementRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reportRepository = FakeReportRepository()
        authRepository = FakeAuthRepository()
        announcementRepository = FakeAnnouncementRepository()
        
        viewModel = HomeViewModel(
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
            getAllReportsUseCase = GetAllReportsUseCase(reportRepository),
            getAllAnnouncementsUseCase = GetAllAnnouncementsUseCase(announcementRepository),
            reportRepository = reportRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `reportCountInProgress should accurately count IN_PROGRESS reports`() = runTest {
        val reports = listOf(
            Report("1", "A", "L1", "D1", UrgencyLevel.LOW, ReportStatus.IN_PROGRESS, 0),
            Report("2", "B", "L2", "D2", UrgencyLevel.LOW, ReportStatus.IN_PROGRESS, 0),
            Report("3", "C", "L3", "D3", UrgencyLevel.LOW, ReportStatus.PENDING, 0)
        )
        reportRepository.setReports(reports)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(2, (state as HomeUiState.Success).reportCountInProgress)
    }

    @Test
    fun `recentReports should only take the last 3 reports and reverse them`() = runTest {
        val reports = listOf(
            Report("1", "A", "L1", "D1", UrgencyLevel.LOW, ReportStatus.PENDING, 1),
            Report("2", "B", "L2", "D2", UrgencyLevel.LOW, ReportStatus.PENDING, 2),
            Report("3", "C", "L3", "D3", UrgencyLevel.LOW, ReportStatus.PENDING, 3),
            Report("4", "D", "L4", "D4", UrgencyLevel.LOW, ReportStatus.PENDING, 4)
        )
        reportRepository.setReports(reports)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(3, state.recentReports.size)
        assertEquals("4", state.recentReports[0].id)
        assertEquals("3", state.recentReports[1].id)
        assertEquals("2", state.recentReports[2].id)
    }
}
