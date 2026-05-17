package com.example.raillog.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TechnicalDocument(
    val id: Long = 0,
    val title: String,
    val documentType: DocumentType,
    val content: String,
    val linkedItemId: Long? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val aiSummary: String? = null,
    val createdAt: Instant = Clock.System.now()
)

enum class DocumentType(val displayName: String) {
    SPEC_SHEET("Lembar Spesifikasi"),
    INSPECTION_REPORT("Laporan Inspeksi"),
    DELIVERY_NOTE("Surat Jalan"),
    COMPLIANCE_CERT("Sertifikat Kepatuhan"),
    MAINTENANCE_LOG("Log Pemeliharaan");

    companion object {
        fun fromString(value: String) = entries.find { it.name == value } ?: SPEC_SHEET
    }
}

enum class VerificationStatus(val displayName: String) {
    UNVERIFIED("Belum Diverifikasi"),
    AI_REVIEWED("Ditinjau AI"),
    APPROVED("Disetujui"),
    FLAGGED("Perlu Perhatian");

    companion object {
        fun fromString(value: String) = entries.find { it.name == value } ?: UNVERIFIED
    }
}