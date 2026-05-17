package com.example.Roomie.domain.model

enum class BookingStatus(val displayName: String) {
    PENDING("Menunggu Persetujuan"),
    APPROVED("Disetujui"),
    REJECTED("Ditolak"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan")
}

data class Booking(
    val id: String,
    val roomId: String,
    val roomName: String,
    val buildingName: String,
    val startTime: Long,
    val endTime: Long,
    val status: BookingStatus,
    val subject: String? = null
)
