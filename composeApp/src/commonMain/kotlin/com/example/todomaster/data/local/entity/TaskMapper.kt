package com.example.todomaster.data.local.entity

import com.example.todomaster.data.local.TaskEntity
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task

fun TaskEntity.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = Quadrant.fromValue(priority_type),
        dueDate = due_date,
        isCompleted = is_completed == 1L,
        isPinned = is_pinned == 1L,
        subTasks = sub_tasks?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        createdAt = created_at
    )
}