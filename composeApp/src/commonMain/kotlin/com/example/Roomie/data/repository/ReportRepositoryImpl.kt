package com.example.Roomie.data.repository

import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ReportRepositoryImpl(
    database: RoomieDatabase
) : ReportRepository {
    private val queries = database.reportQueries

    override fun getAllReports(): Flow<List<Report>> {
        return queries.getAllReports()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Report(
                        id = entity.id,
                        category = entity.category,
                        location = entity.location,
                        description = entity.description,
                        urgency = UrgencyLevel.valueOf(entity.urgency),
                        status = ReportStatus.valueOf(entity.status),
                        createdAt = entity.createdAt
                    )
                }
            }
    }

    override suspend fun updateReportStatus(reportId: String, status: ReportStatus) {
        queries.updateReportStatus(status.name, reportId)
    }

    override suspend fun submitReport(report: Report) {
        queries.insertReport(
            id = report.id,
            category = report.category,
            location = report.location,
            description = report.description,
            urgency = report.urgency.name,
            status = report.status.name,
            createdAt = report.createdAt
        )
    }

    suspend fun seedDummyReports() {
        withContext(Dispatchers.IO) {
            val existing = queries.getAllReports().executeAsList()
            if (existing.isEmpty()) {
                submitReport(
                    Report(
                        id = "1",
                        category = "Gedung Kuliah",
                        location = "GKU 2 - 101",
                        description = "AC Mati",
                        urgency = UrgencyLevel.MEDIUM,
                        status = ReportStatus.IN_PROGRESS,
                        createdAt = 1715673600000
                    )
                )
                submitReport(
                    Report(
                        id = "2",
                        category = "Fasilitas Umum",
                        location = "Kantin",
                        description = "Keran bocor",
                        urgency = UrgencyLevel.LOW,
                        status = ReportStatus.PENDING,
                        createdAt = 1715760000000
                    )
                )
            }
        }
    }
}
