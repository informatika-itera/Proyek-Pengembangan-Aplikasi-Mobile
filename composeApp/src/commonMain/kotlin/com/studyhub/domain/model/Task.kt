package com.studyhub.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Long? = null,
    val title: String,
    val description: String,
    val category: String, // e.g., "Mobile App", "Website"
    val priority: String, // e.g., "High", "Strategic"
    val progress: Int = 0, // 0 to 100
    val dueDate: String, // Simplified as String for now
    val startTime: String,
    val endTime: String,
    val colorHex: Long
)
