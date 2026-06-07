package com.studymate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val createdAt: Long
)
