package com.example.Roomie.domain.usecase

import com.example.Roomie.data.repository.FakeReportRepository
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportUseCasesTest {
    private lateinit var repository: FakeReportRepository

    @BeforeTest
    fun setup() {
        repository = FakeReportRepository()
    }

    @Test
    fun `GetAllReportsUseCase should return flow of reports`() = runTest {
        val useCase = GetAllReportsUseCase(repository)
        val report = Report("1", "A", "L1", "D1", UrgencyLevel.LOW, ReportStatus.PENDING, 0)
        repository.submitReport(report)
        
        val result = useCase().first()
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }

    @Test
    fun `UpdateReportStatusUseCase should update repository`() = runTest {
        val report = Report("1", "A", "L1", "D1", UrgencyLevel.LOW, ReportStatus.PENDING, 0)
        repository.submitReport(report)
        
        val useCase = UpdateReportStatusUseCase(repository)
        useCase("1", ReportStatus.DONE)
        
        val result = repository.getAllReports().first()
        assertEquals(ReportStatus.DONE, result[0].status)
    }
}
