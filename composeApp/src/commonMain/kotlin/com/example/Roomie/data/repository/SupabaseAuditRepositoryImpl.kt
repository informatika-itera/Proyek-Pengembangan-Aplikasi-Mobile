package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.AuditLog
import com.example.Roomie.domain.repository.AuditRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data class AuditLogDto(
    val id: String,
    val room_id: String,
    val room_name: String,
    val user_id: String,
    val user_name: String,
    val action: String,
    val timestamp: Long,
    val details: String? = null
)

class SupabaseAuditRepositoryImpl(
    private val client: SupabaseClient
) : AuditRepository {

    @OptIn(SupabaseExperimental::class)
    override fun getAuditLogs(): Flow<List<AuditLog>> {
        return client.postgrest["audit_logs"].selectAsFlow(
            primaryKey = AuditLogDto::id
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addAuditLog(log: AuditLog): Result<Unit> {
        return try {
            client.postgrest["audit_logs"].insert(log.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun AuditLogDto.toDomain() = AuditLog(
        id = id,
        roomId = room_id,
        roomName = room_name,
        userId = user_id,
        userName = user_name,
        action = action,
        timestamp = timestamp,
        details = details
    )

    private fun AuditLog.toDto() = AuditLogDto(
        id = id,
        room_id = roomId,
        room_name = roomName,
        user_id = userId,
        user_name = userName,
        action = action,
        timestamp = timestamp,
        details = details
    )
}
