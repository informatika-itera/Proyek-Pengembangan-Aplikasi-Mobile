package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import com.example.Roomie.domain.repository.ReportRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data class ReportDto(
    val id: String,
    val category: String,
    val location: String,
    val description: String,
    val urgency: String,
    val status: String,
    val created_at: Long,
    val image_url: String? = null
)

class SupabaseReportRepositoryImpl(
    private val client: SupabaseClient
) : ReportRepository {

    @OptIn(SupabaseExperimental::class)
    override fun getAllReports(): Flow<List<Report>> {
        return client.postgrest["reports"].selectAsFlow(
            primaryKey = ReportDto::id
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateReportStatus(reportId: String, status: ReportStatus) {
        client.postgrest["reports"].update(mapOf("status" to status.name)) {
            filter { eq("id", reportId) }
        }
    }

    override suspend fun submitReport(report: Report) {
        client.postgrest["reports"].insert(report.toDto())
    }

    private fun ReportDto.toDomain() = Report(
        id = id,
        category = category,
        location = location,
        description = description,
        urgency = UrgencyLevel.valueOf(urgency),
        status = ReportStatus.valueOf(status),
        createdAt = created_at,
        imageUrl = image_url
    )

    private fun Report.toDto() = ReportDto(
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
