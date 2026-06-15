package com.studyhub.presentation.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.*
import com.studyhub.domain.repository.PomodoroRepository
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.core.util.currentTimeMillis
import com.studyhub.core.util.toLocalDate
import com.studyhub.core.util.atStartOfDayMillis
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

sealed interface ReportUiState {
    object Loading : ReportUiState
    data class Success(val report: LearningReport) : ReportUiState
    object Empty : ReportUiState
}

class ReportViewModel(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val pomodoroRepository: PomodoroRepository,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    fun loadReport() {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                combine(
                    getAllTasksUseCase(),
                    getUserPreferencesUseCase()
                ) { allTasks: List<Task>, prefs: UserPreferences ->
                    val tasks = allTasks.filter { !it.isDeleted }
                    if (tasks.isEmpty()) {
                        return@combine ReportUiState.Empty
                    }

                    val now = currentTimeMillis()
                    val completed = tasks.filter { it.status == TaskStatus.DONE }
                    val overdue = tasks.filter { it.dueDate < now && it.status != TaskStatus.DONE }

                    // ── Focus data from Pomodoro ──
                    val focusToday = try {
                        pomodoroRepository.getTodayFocusMinutes()
                    } catch (e: Exception) { 0 }

                    // ── Daily focus for last 7 days ──
                    val dayLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    
                    val dailyFocus = (0..6).map { i ->
                        val date = today.minus(6 - i, DateTimeUnit.DAY)
                        val start = date.atStartOfDayMillis()
                        val end = start + 86_400_000L
                        val mins = try {
                            pomodoroRepository.getFocusMinutesInRange(start, end)
                        } catch (e: Exception) { 0 }
                        
                        DailyFocusItem(
                            dayLabel = dayLabels[date.dayOfWeek.isoDayNumber - 1],
                            minutes = mins,
                            isToday = date == today
                        )
                    }

                    // ── Consistency trend ──
                    val consistency = (0..6).map { i ->
                        val date = today.minus(6 - i, DateTimeUnit.DAY)
                        val start = date.atStartOfDayMillis()
                        val end = start + 86_400_000L
                        val sessions = try {
                            pomodoroRepository.getSessionsInRange(start, end)
                        } catch (e: Exception) { emptyList() }
                        
                        // Score calculation: fraction of completed focus sessions (up to 1.0)
                        val score = if (sessions.isEmpty()) 0f 
                                   else (sessions.count { it.wasCompleted }.toFloat() / 4f).coerceAtMost(1f)
                        
                        ConsistencyItem(
                            dayLabel = dayLabels[date.dayOfWeek.isoDayNumber - 1].take(1),
                            score = score
                        )
                    }

                    // ── Weekly activity ──
                    val firstDayOfMonth = today.minus(today.dayOfMonth - 1, DateTimeUnit.DAY)
                    val daysInMonth = today.month.number.let { month ->
                        if (month == 2 && today.year % 4 == 0 && (today.year % 100 != 0 || today.year % 400 == 0)) 29
                        else when (month) {
                            2 -> 28
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                    }
                    
                    val weeklyActivity = mutableListOf<WeeklyActivityItem>()
                    var currentStart = firstDayOfMonth
                    var weekNum = 1
                    
                    while (currentStart.month == today.month) {
                        val startMillis = currentStart.atStartOfDayMillis()
                        val daysToNextMonday = 8 - currentStart.dayOfWeek.isoDayNumber
                        val nextMonday = currentStart.plus(daysToNextMonday, DateTimeUnit.DAY)
                        
                        val endMillis = if (nextMonday.month == today.month) {
                            nextMonday.atStartOfDayMillis()
                        } else {
                            firstDayOfMonth.plus(daysInMonth, DateTimeUnit.DAY).atStartOfDayMillis()
                        }
                        
                        weeklyActivity.add(
                            WeeklyActivityItem(
                                weekLabel = "W$weekNum",
                                added = tasks.count { it.createdAt in startMillis until endMillis },
                                completed = completed.count { it.completedAt != null && it.completedAt in startMillis until endMillis },
                                overdue = tasks.count { it.status != TaskStatus.DONE && it.dueDate in startMillis until endMillis && it.dueDate < now }
                            )
                        )
                        
                        if (nextMonday.month != today.month) break
                        currentStart = nextMonday
                        weekNum++
                    }

                    // ── Subject breakdown ──
                    val subjectMap = tasks.groupBy { it.displaySubject }
                    val subjectProgress = subjectMap.map { (subj, list) ->
                        val done = list.count { it.status == TaskStatus.DONE }
                        val late = list.count { it.dueDate < now && it.status != TaskStatus.DONE }
                        SubjectReportItem(
                            subject = subj,
                            total = list.size,
                            completed = done,
                            overdue = late,
                            completionRate = if (list.isNotEmpty()) done.toFloat() / list.size else 0f
                        )
                    }.sortedByDescending { it.total }

                    // ── Priority breakdown ──
                    val highTasks = tasks.filter { it.priority == Priority.HIGH }
                    val medTasks = tasks.filter { it.priority == Priority.MEDIUM }
                    val lowTasks = tasks.filter { it.priority == Priority.LOW }

                    // ── Completion status calculation for Donut Chart ──
                    val onTime = completed.count { t ->
                        t.completedAt == null || t.completedAt <= t.dueDate
                    }
                    val finishedLate = completed.count { t ->
                        t.completedAt != null && t.completedAt > t.dueDate
                    }
                    val overdueNotDone = tasks.count { it.status != TaskStatus.DONE && it.dueDate < now }
                    
                    // Total "Terlambat" includes finished late and currently overdue tasks
                    val late = finishedLate + overdueNotDone
                    
                    // "Belum Selesai" only includes non-done tasks that are still within deadline
                    val pending = tasks.count { it.status != TaskStatus.DONE && it.dueDate >= now }

                    // ── Streak calculation ──
                    val streak = prefs.currentStreak

                    // ── AI Insight ──
                    val completionPct = (completed.size.toFloat() / tasks.size * 100).toInt()
                    val insight = generateInsight(completionPct, overdue.size, streak, focusToday)

                    ReportUiState.Success(
                        LearningReport(
                            totalTasks = tasks.size,
                            completedTasks = completed.size,
                            overdueTasks = overdue.size,
                            completionRate = if (tasks.isNotEmpty()) completed.size.toFloat() / tasks.size else 0f,
                            totalFocusMinutes = focusToday,
                            averageDailyFocusMinutes = if (dailyFocus.isNotEmpty()) dailyFocus.sumOf { it.minutes } / 7 else 0,
                            currentStreak = streak,
                            longestStreak = prefs.longestStreak,
                            subjectProgress = subjectProgress,
                            weeklyActivity = weeklyActivity,
                            dailyFocusDuration = dailyFocus,
                            focusConsistencyTrend = consistency,
                            taskCompletionByStatus = TaskCompletionStatus(onTime, late, pending),
                            priorityBreakdown = PriorityBreakdown(
                                highTotal = highTasks.size,
                                highCompleted = highTasks.count { it.status == TaskStatus.DONE },
                                mediumTotal = medTasks.size,
                                mediumCompleted = medTasks.count { it.status == TaskStatus.DONE },
                                lowTotal = lowTasks.size,
                                lowCompleted = lowTasks.count { it.status == TaskStatus.DONE }
                            ),
                            aiInsight = insight
                        )
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Empty
            }
        }
    }

    private fun calculateStreak(completed: List<Task>): Int {
        val tz = TimeZone.currentSystemDefault()
        // Convert completed timestamps to unique dates (LocalDate)
        val completionDates = completed
            .mapNotNull { it.completedAt }
            .map { Instant.fromEpochMilliseconds(it).toLocalDateTime(tz).date }
            .toSet()
            .sortedDescending()

        if (completionDates.isEmpty()) return 0

        val today = Clock.System.now().toLocalDateTime(tz).date
        val yesterday = today.minus(1, DateTimeUnit.DAY)

        // If no tasks completed today OR yesterday, the streak is broken
        if (!completionDates.contains(today) && !completionDates.contains(yesterday)) {
            return 0
        }

        var streak = 0
        var currentCheckDate = if (completionDates.contains(today)) today else yesterday

        for (date in completionDates) {
            if (date == currentCheckDate) {
                streak++
                currentCheckDate = currentCheckDate.minus(1, DateTimeUnit.DAY)
            } else if (date < currentCheckDate) {
                // Gap found
                break
            }
        }

        return streak
    }

    private fun generateInsight(pct: Int, overdue: Int, streak: Int, focus: Int): String = when {
        pct >= 80 && streak >= 7 ->
            "Luar biasa! Kamu di jalur yang tepat dengan $pct% tugas selesai dan streak $streak hari."
        pct >= 60 ->
            "Progres bagus! $pct% tugas selesai. " +
                    if (overdue > 0) "Fokus selesaikan $overdue tugas yang terlambat."
                    else "Pertahankan konsistensimu!"
        overdue > 3 ->
            "Ada $overdue tugas terlambat. Prioritaskan tugas HIGH priority terlebih dahulu."
        streak >= 3 ->
            "Streak $streak hari! Konsistensimu meningkat. Target selesaikan ${100 - pct}% tugas tersisa."
        else ->
            "Mulai dengan tugas prioritas tinggi. Gunakan Pomodoro untuk fokus lebih baik."
    }
}
