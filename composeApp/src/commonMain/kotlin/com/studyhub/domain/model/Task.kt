package com.studyhub.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val subject: String,
    val priority: Priority,
    val status: TaskStatus,
    val dueDate: Long,
    val dueTime: String?,
    val tags: List<String>,
    val estimatedMinutes: Int,
    val isDeleted: Boolean,
    val completedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
) {
    val displaySubject: String get() = subject.ifBlank { "Lainnya" }
}

@Immutable
enum class Priority { HIGH, MEDIUM, LOW }

@Immutable
enum class TaskStatus(val value: String) {
    TODO("todo"),
    IN_PROGRESS("in_progress"),
    DONE("done");

    companion object {
        fun fromString(value: String): TaskStatus =
            entries.firstOrNull { it.value == value } ?: TODO
    }
}

@Immutable
enum class SortBy { DUE_DATE, PRIORITY, SUBJECT, TITLE }
