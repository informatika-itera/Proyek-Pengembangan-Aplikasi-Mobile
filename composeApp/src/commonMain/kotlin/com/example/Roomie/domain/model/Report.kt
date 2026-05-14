package com.example.Roomie.domain.model

enum class ReportStatus(val displayName: String) {
    PENDING("Menunggu"),
    IN_PROGRESS("Diproses"),
    DONE("Selesai")
}

enum class UrgencyLevel(val displayName: String) {
    LOW("Rendah"),
    MEDIUM("Sedang"),
    HIGH("Tinggi")
}

data class Report(
    val id: String,
    val category: String,
    val location: String,
    val description: String,
    val urgency: UrgencyLevel,
    val status: ReportStatus,
    val createdAt: Long
)
