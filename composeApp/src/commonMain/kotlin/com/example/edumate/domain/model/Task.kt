package com.example.edumate.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val deadline: Instant? = null,
    val isCompleted: Boolean = false,
    val subTasks: String = "",
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val isEmpty: Boolean
        get() = title.isBlank() && description.isBlank()
}

enum class TaskPriority(val displayName: String) {
    LOW("Rendah"),
    MEDIUM("Sedang"),
    HIGH("Tinggi"),
    URGENT("Mendesak");

    companion object {
        fun fromString(value: String): TaskPriority {
            return entries.find { it.name == value } ?: MEDIUM
        }
    }
}