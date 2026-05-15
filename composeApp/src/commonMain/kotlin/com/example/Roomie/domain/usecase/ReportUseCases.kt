package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow

class GetAllReportsUseCase(private val repository: ReportRepository) {
    operator fun invoke(): Flow<List<Report>> = repository.getAllReports()
}

class UpdateReportStatusUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(reportId: String, status: ReportStatus) = 
        repository.updateReportStatus(reportId, status)
}

class SubmitReportUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(report: Report) = repository.submitReport(report)
}
