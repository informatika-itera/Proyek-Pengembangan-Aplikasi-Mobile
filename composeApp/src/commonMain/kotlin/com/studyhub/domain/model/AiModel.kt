package com.studyhub.domain.model

data class PriorityResult(
    val taskId: String,
    val priorityOrder: Int,
    val reason: String
)

data class ReminderSchedule(
    val taskId: String,
    val suggestedReminderTime: Long,
    val adaptiveReason: String
)
