package com.studyhub.domain.model

import kotlinx.serialization.Serializable

// Compressed task for AI prompt — hemat token
@Serializable
data class AiTaskSummary(
    val i: String,     // id dipersingkat 8 char
    val t: String,     // title max 30 char
    val s: String,     // subject max 15 char
    val p: String,     // priority: H/M/L
    val due: Long,     // jam hingga deadline (bukan epoch)
    val est: Int,      // estimatedMinutes
    val st: String     // status: T/I/D
)

@Serializable
data class PriorityResult(
    val taskId: String,
    val priorityOrder: Int,
    val reason: String   // max 10 kata
)

@Serializable
data class ReminderSchedule(
    val taskId: String,
    val suggestedReminderTime: Long,   // epoch millis
    val adaptiveReason: String         // max 10 kata
)

data class AiUsageStats(
    val priorityCallsToday: Int,
    val reminderCallsToday: Int,
    val lastResetDate: String  // "yyyy-MM-dd"
)

fun Task.toAiSummary() = AiTaskSummary(
    i = id.take(8),
    t = title.take(30),
    s = subject.take(15),
    p = when (priority) {
        Priority.HIGH -> "H"
        Priority.MEDIUM -> "M"
        Priority.LOW -> "L"
    },
    due = maxOf(0L,
        (dueDate - com.studyhub.core.util.currentTimeMillis()) / 3_600_000
    ),
    est = estimatedMinutes,
    st = when (status) {
        TaskStatus.TODO -> "T"
        TaskStatus.IN_PROGRESS -> "I"
        TaskStatus.DONE -> "D"
    }
)
