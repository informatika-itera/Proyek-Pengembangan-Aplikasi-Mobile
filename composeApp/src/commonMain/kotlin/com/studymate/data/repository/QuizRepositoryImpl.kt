package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.studymate.data.local.StudyMateDatabase
import com.studymate.domain.model.QuizHistory
import com.studymate.domain.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuizRepositoryImpl(
    private val database: StudyMateDatabase
) : QuizRepository {
    private val queries = database.quizQueries

    override fun getAllHistory(): Flow<List<QuizHistory>> {
        return queries.selectAllHistory()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    QuizHistory(
                        id = entity.id,
                        noteId = entity.noteId,
                        noteTitle = entity.noteTitle,
                        subject = entity.subject,
                        score = entity.score.toInt(),
                        totalQuestions = entity.totalQuestions.toInt(),
                        createdAt = entity.createdAt
                    )
                }
            }
    }

    override suspend fun insertHistory(history: QuizHistory) {
        queries.insertHistory(
            noteId = history.noteId,
            noteTitle = history.noteTitle,
            subject = history.subject,
            score = history.score.toLong(),
            totalQuestions = history.totalQuestions.toLong(),
            createdAt = history.createdAt
        )
    }

    override suspend fun deleteHistory(id: Long) {
        queries.deleteHistory(id)
    }
}
