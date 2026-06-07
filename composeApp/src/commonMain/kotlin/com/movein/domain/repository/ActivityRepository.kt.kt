package com.movein.domain.repository

import com.movein.domain.model.ActivityModel
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAllActivities(): Flow<List<ActivityModel>>

    suspend fun generateAndSaveActivity(mood: String): ActivityModel
}