package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeReportRepository : ReportRepository {
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    
    override fun getAllReports(): Flow<List<Report>> = _reports.asStateFlow()

    override suspend fun updateReportStatus(reportId: String, status: ReportStatus) {
        _reports.update { current ->
            current.map { if (it.id == reportId) it.copy(status = status) else it }
        }
    }

    override suspend fun submitReport(report: Report) {
        _reports.update { it + report }
    }
    
    fun setReports(reports: List<Report>) {
        _reports.value = reports
    }
}
