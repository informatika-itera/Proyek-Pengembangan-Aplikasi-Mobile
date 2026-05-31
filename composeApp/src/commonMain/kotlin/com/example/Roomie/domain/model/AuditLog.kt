package com.example.Roomie.domain.model

data class AuditLog(
    val id: String,
    val roomId: String,
    val roomName: String,
    val userId: String,
    val userName: String,
    val action: String, // e.g., "APPROVED", "OVERRIDE"
    val timestamp: Long,
    val details: String? = null
)
