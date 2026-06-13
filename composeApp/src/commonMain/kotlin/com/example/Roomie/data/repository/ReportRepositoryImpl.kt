package com.example.Roomie.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import io.github.aakira.napier.Napier

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
                        // 1. Initial Sync
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

                        // 2. Realtime Listener
                        supabaseClient.realtime.connect()
                        val channel = supabaseClient.realtime.channel("reports-sync")
                        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                            table = "reports"
                        }

                        channel.subscribe()

                        changeFlow.collect { change ->
                            withContext(Dispatchers.IO) {
                                when (change) {
                                    is PostgresAction.Insert -> {
                                        val dto = change.decodeRecord<ReportRemoteDto>()
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
                                    is PostgresAction.Update -> {
                                        val dto = change.decodeRecord<ReportRemoteDto>()
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
                                    is PostgresAction.Delete -> {
                                        val id = change.oldRecord["id"]?.jsonPrimitive?.contentOrNull
                                        if (id != null) queries.deleteReport(id)
                                    }
                                    else -> {}
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Napier.e("Report Sync Error: ${e.message}", e)
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
                Napier.e("Update Report Status Error: ${e.message}", e)
                // update locally anyway
                queries.updateReportStatus(status.name, reportId)
            }
        }
    }

    override suspend fun submitReport(report: Report) {
        withContext(Dispatchers.IO) {
            try {
                // 🌐 CLOUD SAVE (MANDATORY)
                if (networkMonitor.isOnline.value) {
                    supabaseClient.postgrest["reports"].insert(report.toDto())
                    Napier.d("Report saved to Cloud: ${report.id}")
                } else {
                    Napier.w("Offline: Saving report locally only. Will not be visible to others until sync.")
                }
                
                // 💾 LOCAL SAVE
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
                Napier.e("Submit Report Error: ${e.message}", e)
                // local fallback
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
}
