package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.studymate.data.local.StudyMateDatabase
import com.studymate.domain.model.ActivityDay
import com.studymate.domain.repository.ActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*

class ActivityRepositoryImpl(
    private val database: StudyMateDatabase
) : ActivityRepository {

    override fun getActivityHeatmap(days: Int): Flow<List<ActivityDay>> {
        val startDate = Clock.System.now()
            .minus(days, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.toString()
            
        return database.activityQueries.getActivityHeatmap(startDate)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map {
                    ActivityDay(
                        date = it.date,
                        quizCount = it.quizCount.toInt(),
                        notesCount = it.notesCount.toInt(),
                        totalPoints = it.totalPoints.toInt()
                    )
                }
            }
    }

    override suspend fun recordQuizCompletion() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        database.activityQueries.incrementQuizCount(today)
    }

    override suspend fun recordNoteCreation() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        database.activityQueries.incrementNotesCount(today)
    }

    override suspend fun getMonthlyQuizCount(): Int {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val endDate = now.date.toString()
        val startDate = now.date.minus(1, DateTimeUnit.MONTH).toString()
        
        return try {
            database.activityQueries.getTotalQuizCountInDateRange(startDate, endDate)
                .executeAsOne().totalQuiz?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }
}
