package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<Trip>>
    fun getTripById(id: String): Flow<Trip?>
    suspend fun saveTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
    fun getProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
}
