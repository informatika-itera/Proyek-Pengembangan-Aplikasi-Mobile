package com.example.Roomie.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
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
                        urgency = try { UrgencyLevel.valueOf(entity.urgency) } catch (e: Exception) { UrgencyLevel.LOW },
                        status = try { ReportStatus.valueOf(entity.status) } catch (e: Exception) { ReportStatus.PENDING },
                        createdAt = entity.createdAt,
                        imageUrl = entity.imageUrl
                    )
                }
            }
    }

    override suspend fun updateReportStatus(reportId: String, status: ReportStatus) {
        withContext(Dispatchers.IO) {
            queries.updateReportStatus(status.name, reportId)
        }
    }

    override suspend fun submitReport(report: Report) {
        withContext(Dispatchers.IO) {
            queries.insertReport(
                id = report.id,
                category = report.category,
                location = report.location,
                description = report.description,
                urgency = report.urgency.name,
                status = report.status.name,
                createdAt = report.createdAt,
                imageUrl = report.imageUrl
            )
        }
    }

    suspend fun seedDummyReports() {
        withContext(Dispatchers.IO) {
            val existing = queries.getAllReports().executeAsList()
            if (existing.isEmpty()) {
                val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                submitReport(
                    Report(
                        id = "R1",
                        category = "Fasilitas",
                        location = "GKU 2 - 101",
                        description = "AC tidak dingin",
                        urgency = UrgencyLevel.MEDIUM,
                        status = ReportStatus.PENDING,
                        createdAt = now - 86400000
                    )
                )
            }
        }
    }
}
