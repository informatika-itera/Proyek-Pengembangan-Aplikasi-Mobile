package com.studymate.domain.repository

import com.studymate.domain.model.QuizHistory
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getAllHistory(): Flow<List<QuizHistory>>
    suspend fun insertHistory(history: QuizHistory)
    suspend fun deleteHistory(id: Long)
}
