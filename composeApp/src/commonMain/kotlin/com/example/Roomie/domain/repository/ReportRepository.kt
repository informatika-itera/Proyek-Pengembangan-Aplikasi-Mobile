package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getAllReports(): Flow<List<Report>>
    suspend fun updateReportStatus(reportId: String, status: ReportStatus)
    suspend fun submitReport(report: Report)
}
