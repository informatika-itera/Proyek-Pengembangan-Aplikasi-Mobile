package com.example.todomaster.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val priority: Quadrant,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val isPinned: Boolean = false,
    val subTasks: List<String> = emptyList(),
    val createdAt: Long
)