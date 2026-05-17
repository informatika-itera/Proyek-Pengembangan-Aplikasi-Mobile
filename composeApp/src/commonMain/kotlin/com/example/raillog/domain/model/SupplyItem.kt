package com.example.raillog.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class SupplyItem(
    val id: Long = 0,
    val partCode: String,
    val name: String,
    val category: PartCategory,
    val quantity: Int,
    val unit: String,
    val supplier: String,
    val status: SupplyStatus = SupplyStatus.PENDING,
    val priority: Priority = Priority.NORMAL,
    val documentRef: String? = null,
    val notes: String = "",
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)

enum class PartCategory(val displayName: String) {
    BOGIE("Bogie & Roda"),
    PROPULSION("Propulsi & Motor"),
    BRAKING("Sistem Pengereman"),
    ELECTRICAL("Kelistrikan"),
    BODY("Bodi & Struktur"),
    INTERIOR("Interior"),
    SAFETY("Keselamatan"),
    MAINTENANCE("Pemeliharaan");

    companion object {
        fun fromString(value: String) = entries.find { it.name == value } ?: MAINTENANCE
    }
}

enum class SupplyStatus(val displayName: String) {
    PENDING("Menunggu"),
    IN_TRANSIT("Dalam Pengiriman"),
    RECEIVED("Diterima"),
    VERIFIED("Terverifikasi"),
    REJECTED("Ditolak");

    companion object {
        fun fromString(value: String) = entries.find { it.name == value } ?: PENDING
    }
}

enum class Priority(val displayName: String) {
    LOW("Rendah"),
    NORMAL("Normal"),
    HIGH("Tinggi"),
    CRITICAL("Kritis");

    companion object {
        fun fromString(value: String) = entries.find { it.name == value } ?: NORMAL
    }
}