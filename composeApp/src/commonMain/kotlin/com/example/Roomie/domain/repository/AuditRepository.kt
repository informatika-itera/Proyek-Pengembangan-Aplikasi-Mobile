package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.AuditLog
import kotlinx.coroutines.flow.Flow

interface AuditRepository {
    fun getAuditLogs(): Flow<List<AuditLog>>
    suspend fun addAuditLog(log: AuditLog): Result<Unit>
}
