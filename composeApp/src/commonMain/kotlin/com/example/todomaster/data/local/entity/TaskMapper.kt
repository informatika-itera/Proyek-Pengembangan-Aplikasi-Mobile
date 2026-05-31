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
        createdAt = created_at,
        isAiGenerated = isAiGenerated == 1L,
        parentTaskTitle = parentTaskTitle
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id ?: 0L,
        title = title,
        description = description,
        priority_type = priority.value,
        due_date = dueDate,
        is_completed = if (isCompleted) 1L else 0L,
        is_pinned = if (isPinned) 1L else 0L,
        sub_tasks = if (subTasks.isEmpty()) null else subTasks.joinToString(","),
        created_at = createdAt,
        isAiGenerated = if (isAiGenerated) 1L else 0L,
        parentTaskTitle = parentTaskTitle
    )
}