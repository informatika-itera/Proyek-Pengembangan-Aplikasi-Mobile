package com.example.Roomie.data.repository

import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class ReportRemoteDto(
    val id: String,
    val category: String,
    val location: String,
    val description: String,
    val urgency: String,
    val status: String,
    val created_at: Long,
    val image_url: String? = null
)

class ReportRepositoryImpl(
    database: RoomieDatabase,
    private val supabaseClient: SupabaseClient,
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope
) : ReportRepository {
    private val queries = database.reportQueries

    init {
        observeSync()
    }

    private fun observeSync() {
        scope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    try {
                        val remoteReports = supabaseClient.postgrest["reports"]
                            .select().decodeList<ReportRemoteDto>()
                        
                        withContext(Dispatchers.IO) {
                            remoteReports.forEach { dto ->
                                queries.insertReport(
                                    id = dto.id,
                                    category = dto.category,
                                    location = dto.location,
                                    description = dto.description,
                                    urgency = dto.urgency,
                                    status = dto.status,
                                    createdAt = dto.created_at,
                                    imageUrl = dto.image_url
                                )
                            }
                        }
                    } catch (e: Exception) {
                        // silent fail
                    }
                }
            }
        }
    }

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
            try {
                if (networkMonitor.isOnline.value) {
                    supabaseClient.postgrest["reports"].update(mapOf("status" to status.name)) {
                        filter { eq("id", reportId) }
                    }
                }
                queries.updateReportStatus(status.name, reportId)
            } catch (e: Exception) {
                queries.updateReportStatus(status.name, reportId)
            }
        }
    }

    override suspend fun submitReport(report: Report) {
        withContext(Dispatchers.IO) {
            try {
                if (networkMonitor.isOnline.value) {
                    supabaseClient.postgrest["reports"].insert(report.toDto())
                }
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
            } catch (e: Exception) {
                // save locally anyway
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
    }

    private fun Report.toDto() = ReportRemoteDto(
        id = id,
        category = category,
        location = location,
        description = description,
        urgency = urgency.name,
        status = status.name,
        created_at = createdAt,
        image_url = imageUrl
    )

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
