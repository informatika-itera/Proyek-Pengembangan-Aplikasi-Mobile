package com.studyhub.domain.model

data class LearningReport(
    val totalTasks: Int,
    val completedTasks: Int,
    val overdueTasks: Int,
    val completionRate: Float,
    val totalFocusMinutes: Int,
    val averageDailyFocusMinutes: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val subjectProgress: List<SubjectReportItem>,
    val weeklyActivity: List<WeeklyActivityItem>,
    val dailyFocusDuration: List<DailyFocusItem>,
    val focusConsistencyTrend: List<ConsistencyItem>,
    val taskCompletionByStatus: TaskCompletionStatus,
    val priorityBreakdown: PriorityBreakdown,
    val aiInsight: String = ""
)

data class SubjectReportItem(
    val subject: String,
    val total: Int,
    val completed: Int,
    val overdue: Int,
    val completionRate: Float
)

data class WeeklyActivityItem(
    val weekLabel: String,    // "W1", "W2", etc.
    val added: Int,
    val completed: Int,
    val overdue: Int
)

data class DailyFocusItem(
    val dayLabel: String,     // "Sen", "Sel", etc.
    val minutes: Int,
    val isToday: Boolean = false
)

data class ConsistencyItem(
    val dayLabel: String,
    val score: Float          // 0.0 - 1.0
)

data class TaskCompletionStatus(
    val onTime: Int,
    val late: Int,
    val pending: Int
) {
    val total get() = onTime + late + pending
    val onTimeRate get() = if (total > 0)
        onTime.toFloat() / total else 0f
    val lateRate get() = if (total > 0)
        late.toFloat() / total else 0f
    val pendingRate get() = if (total > 0)
        pending.toFloat() / total else 0f
}

data class PriorityBreakdown(
    val highTotal: Int, val highCompleted: Int,
    val mediumTotal: Int, val mediumCompleted: Int,
    val lowTotal: Int, val lowCompleted: Int
)
