package com.example.pantaujompo.domain.repository

import com.example.pantaujompo.domain.model.ActivityModel
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAllActivities(): Flow<List<ActivityModel>>
    suspend fun getActivityById(id: Long): ActivityModel?
    suspend fun insertActivity(type: String, duration: Long, distance: Double, calories: Long, date: Long, notes: String?)
    suspend fun updateActivity(id: Long, type: String, duration: Long, distance: Double, calories: Long, date: Long, notes: String?)
    suspend fun deleteActivityById(id: Long)
}