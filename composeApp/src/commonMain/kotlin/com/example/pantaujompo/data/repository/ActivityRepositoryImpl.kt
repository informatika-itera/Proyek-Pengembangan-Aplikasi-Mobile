package com.example.pantaujompo.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.pantaujompo.data.local.NoteDatabase // Menggunakan NoteDatabase bawaan setup Gradle
import com.example.pantaujompo.domain.model.ActivityModel
import com.example.pantaujompo.domain.repository.ActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ActivityRepositoryImpl(
    db: NoteDatabase
) : ActivityRepository {

    private val queries = db.activityQueries

    override fun getAllActivities(): Flow<List<ActivityModel>> {
        return queries.getAllActivities()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    ActivityModel(
                        id = entity.id,
                        type = entity.activity_type,
                        durationMinutes = entity.duration_minutes,
                        distanceKm = entity.distance_km,
                        caloriesBurned = entity.calories_burned,
                        dateTimestamp = entity.date_timestamp,
                        notes = entity.notes
                    )
                }
            }
    }

    override suspend fun getActivityById(id: Long): ActivityModel? {
        return withContext(Dispatchers.IO) {
            val entity = queries.getActivityById(id).executeAsOneOrNull()
            entity?.let {
                ActivityModel(
                    id = it.id,
                    type = it.activity_type,
                    durationMinutes = it.duration_minutes,
                    distanceKm = it.distance_km,
                    caloriesBurned = it.calories_burned,
                    dateTimestamp = it.date_timestamp,
                    notes = it.notes
                )
            }
        }
    }

    override suspend fun insertActivity(
        type: String, duration: Long, distance: Double, calories: Long, date: Long, notes: String?
    ) {
        withContext(Dispatchers.IO) {
            queries.insertActivity(
                activity_type = type,
                duration_minutes = duration,
                distance_km = distance,
                calories_burned = calories,
                date_timestamp = date,
                notes = notes
            )
        }
    }

    override suspend fun updateActivity(
        id: Long, type: String, duration: Long, distance: Double, calories: Long, date: Long, notes: String?
    ) {
        withContext(Dispatchers.IO) {
            queries.updateActivity(
                id = id,
                activity_type = type,
                duration_minutes = duration,
                distance_km = distance,
                calories_burned = calories,
                date_timestamp = date,
                notes = notes
            )
        }
    }

    override suspend fun deleteActivityById(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteActivityById(id)
        }
    }
}