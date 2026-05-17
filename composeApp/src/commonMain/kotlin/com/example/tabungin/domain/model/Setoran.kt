package com.example.tabungin.domain.model

data class Setoran(
    val id: Long = 0L,
    val targetId: Long,
    val amount: Double,
    val catatan: String = "",
    val tanggal: String,
    val createdAt: String = "",
    val targetNama: String = ""
)