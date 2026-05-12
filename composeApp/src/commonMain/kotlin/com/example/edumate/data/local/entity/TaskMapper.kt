package com.example.edumate.data.local.entity

import com.example.edumate.data.local.TaskEntity
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.model.TaskPriority
import kotlinx.datetime.Instant

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = TaskPriority.fromString(priority),
        deadline = deadline?.let { Instant.fromEpochMilliseconds(it) },
        isCompleted = is_completed == 1L,
        subTasks = sub_tasks,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

fun List<TaskEntity>.toDomainList(): List<Task> {
    return map { it.toDomain() }
}