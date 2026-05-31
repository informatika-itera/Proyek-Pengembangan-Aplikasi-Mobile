package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.AuditLog
import com.example.Roomie.domain.repository.AuditRepository
import kotlinx.coroutines.flow.Flow

class GetAuditLogsUseCase(private val repository: AuditRepository) {
    operator fun invoke(): Flow<List<AuditLog>> = repository.getAuditLogs()
}

class AddAuditLogUseCase(private val repository: AuditRepository) {
    suspend operator fun invoke(log: AuditLog) = repository.addAuditLog(log)
}
