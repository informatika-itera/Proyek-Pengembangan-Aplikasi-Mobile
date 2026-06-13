package com.movein.data.repository

import com.movein.domain.model.ActivityModel
import com.movein.data.remote.api.GeminiApiService
import com.movein.domain.repository.ActivityRepository
import com.example.movein.data.local.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * Repository implementation utilizing a Cache-First strategy.
 * Binds Ktor Client (Gemini API) and SQLDelight Database.
 */
class ActivityRepositoryImpl(
    private val database: NoteDatabase,
    private val geminiApiService: GeminiApiService
) : ActivityRepository {

    private val queries = database.noteQueries

    // Fetch activities from local database cache as a Flow
    override fun getAllActivities(): Flow<List<ActivityModel>> {
        return flow {
            val localData = queries.getActivitiesByMood("").executeAsList().map { entity ->
                ActivityModel(
                    id = entity.id,
                    mood = entity.mood,
                    title = entity.title,
                    description = entity.description,
                    createdAt = entity.created_at
                )
            }
            emit(localData)
        }.flowOn(Dispatchers.IO)
    }

    // Aligned with the interface contract to return ActivityModel instead of Unit
    override suspend fun generateAndSaveActivity(mood: String): ActivityModel {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Fetch single text recommendation from Gemini AI via API Service
                val response = geminiApiService.generateActivitySuggestion(mood)
                val rawText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Failed to get activity recommendation."

                // 2. Split generated text into Title (first line) and Description
                val lines = rawText.lines().filter { it.isNotBlank() }
                val title = lines.firstOrNull()?.replace("**", "") ?: "Activity for Mood $mood"
                val description = lines.drop(1).joinToString("\n").replace("**", "")
                    .ifBlank { "Do this positive activity to make your day better!" }

                // 3. Save the newly generated AI data into SQLDelight database
                val currentTime = Clock.System.now().toEpochMilliseconds()
                queries.insertActivity(
                    mood = mood,
                    title = title,
                    description = description,
                    created_at = currentTime
                )

                val insertedId = queries.lastInsertId().executeAsOne()

                ActivityModel(
                    id = insertedId,
                    mood = mood,
                    title = title,
                    description = description,
                    createdAt = currentTime
                )
            } catch (e: Exception) {
                // 4. Offline fallback: fetch saved data from SQLDelight database
                val cachedData = getLocalActivitiesByMood(mood)
                if (cachedData.isEmpty()) {
                    throw Exception("Device is offline and no cached activities found for mood: $mood")
                }
                cachedData.first()
            }
        }
    }

    // Helper method to retrieve cached activities by specific mood
    private fun getLocalActivitiesByMood(mood: String): List<ActivityModel> {
        return queries.getActivitiesByMood(mood).executeAsList().map { entity ->
            ActivityModel(
                id = entity.id,
                mood = entity.mood,
                title = entity.title,
                description = entity.description,
                createdAt = entity.created_at
            )
        }
    }
}