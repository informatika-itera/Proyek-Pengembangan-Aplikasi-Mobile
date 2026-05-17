package com.studymate.domain.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Long = 0,
    val title: String,
    val rawContent: String,
    val refinedContent: String? = null,
    val subject: String = "",
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = createdAt,
    val isRefined: Boolean = false
)
