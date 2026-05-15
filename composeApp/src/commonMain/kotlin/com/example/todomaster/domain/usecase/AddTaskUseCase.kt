package com.example.todomaster.domain.usecase

import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class AddTaskUseCase(private val repository: TaskRepository) {

    private val DAILY_DO_FIRST_LIMIT = 5

    suspend operator fun invoke(task: Task): Result<Unit> {
        if (task.priority == Quadrant.DO_FIRST) {
            val now = Clock.System.now()
            val todayStartMillis = now.toLocalDateTime(TimeZone.currentSystemDefault())
                .date.atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()

            val currentCount = repository.getTodayDoFirstCount(todayStartMillis)

            if (currentCount >= DAILY_DO_FIRST_LIMIT) {
                return Result.failure(Exception("Limit harian Do First sudah penuh (Maksimal 5 tugas). Fokus selesaikan yang ada dulu!"))
            }
        }

        repository.insertTask(task)
        return Result.success(Unit)
    }
}