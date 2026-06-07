package com.studyhub.domain.fake

import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import kotlinx.datetime.Clock

object TaskBuilder {
    fun build(
        id: String = "task_1",
        title: String = "Title",
        description: String = "Desc",
        subject: String = "Subject",
        priority: Priority = Priority.MEDIUM,
        status: TaskStatus = TaskStatus.TODO,
        dueDate: Long = Clock.System.now().toEpochMilliseconds(),
        completedAt: Long? = null,
        createdAt: Long = Clock.System.now().toEpochMilliseconds(),
        isDeleted: Boolean = false
    ) = Task(
        id = id,
        title = title,
        description = description,
        subject = subject,
        priority = priority,
        status = status,
        dueDate = dueDate,
        dueTime = null,
        tags = emptyList(),
        estimatedMinutes = 30,
        isDeleted = isDeleted,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = createdAt
    )
}
