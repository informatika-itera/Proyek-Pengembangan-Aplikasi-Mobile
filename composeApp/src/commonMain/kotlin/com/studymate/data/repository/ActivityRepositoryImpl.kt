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
        updateStreak()
    }

    override suspend fun recordNoteCreation() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        database.activityQueries.incrementNotesCount(today)
        updateStreak()
    }

    private suspend fun updateStreak() {
        val now = Clock.System.now().toEpochMilliseconds()
        val profile = database.userProfileQueries.getProfile().executeAsOneOrNull()
        
        if (profile != null) {
            val lastStudyDate = profile.lastStudyDate
            val todayDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            val lastStudyLocalDate = lastStudyDate?.let { 
                Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date 
            }

            if (lastStudyLocalDate == null) {
                database.userProfileQueries.updateStreak(1, now)
            } else if (lastStudyLocalDate < todayDate) {
                val yesterday = todayDate.minus(1, DateTimeUnit.DAY)
                if (lastStudyLocalDate == yesterday) {
                    database.userProfileQueries.updateStreak(profile.currentStreak + 1, now)
                } else {
                    database.userProfileQueries.updateStreak(1, now)
                }
            }
        }
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
