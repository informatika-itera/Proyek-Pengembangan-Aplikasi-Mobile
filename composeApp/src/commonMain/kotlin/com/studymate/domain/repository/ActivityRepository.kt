package com.studymate.domain.repository

import com.studymate.domain.model.ActivityDay
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getActivityHeatmap(days: Int = 30): Flow<List<ActivityDay>>
    suspend fun recordQuizCompletion()
    suspend fun recordNoteCreation()
    suspend fun getMonthlyQuizCount(): Int
}
