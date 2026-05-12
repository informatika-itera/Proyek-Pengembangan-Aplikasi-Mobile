package com.example.pantaujompo.domain.model

import kotlinx.datetime.Instant

// Data class utama untuk log aktivitas
data class FitnessLog(
    val id: Long? = null,
    val type: LogType,
    val title: String,
    val timestamp: Instant,
    val details: Map<String, String>,
    val isAIGenerated: Boolean = false,
    val sourceUrl: String? = null
)

// Kategori Log
enum class LogType {
    RUN, MEAL, PAIN, ARTICLE
}