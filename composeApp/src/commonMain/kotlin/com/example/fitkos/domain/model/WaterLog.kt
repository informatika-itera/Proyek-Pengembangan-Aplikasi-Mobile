package com.example.fitkos.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WaterLog(
    val date: String, // YYYY-MM-DD
    val amount: Int,
    val target: Int = 8
)
