package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.studymate.data.local.StudyMateDatabase
import com.studymate.domain.model.QuizHistory
import com.studymate.domain.model.QuizQuestion
import com.studymate.domain.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class QuizRepositoryImpl(
    private val database: StudyMateDatabase
) : QuizRepository {
    private val queries = database.quizQueries
    private val json = Json { ignoreUnknownKeys = true }

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
                        createdAt = entity.createdAt,
                        questions = try { 
                            json.decodeFromString<List<QuizQuestion>>(entity.serializedQuestions) 
                        } catch (e: Exception) { emptyList() },
                        userAnswers = try { 
                            json.decodeFromString<Map<Int, Int>>(entity.serializedAnswers) 
                        } catch (e: Exception) { emptyMap() }
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
            createdAt = history.createdAt,
            serializedQuestions = json.encodeToString(history.questions),
            serializedAnswers = json.encodeToString(history.userAnswers)
        )
    }

    override suspend fun deleteHistory(id: Long) {
        queries.deleteHistory(id)
    }
}
