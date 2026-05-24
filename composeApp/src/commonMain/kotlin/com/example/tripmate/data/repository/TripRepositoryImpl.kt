package com.example.tripmate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.tripmate.data.local.TripDatabase
import com.example.tripmate.data.local.TripEntity
import com.example.tripmate.domain.model.Trip
import com.example.tripmate.domain.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TripRepositoryImpl(
    database: TripDatabase
) : TripRepository {

    private val queries = database.tripQueries

    private fun TripEntity.toDomain(): Trip {
        return Trip(
            id = id,
            destination = destination,
            startDate = start_date,
            endDate = end_date,
            budget = budget,
            createdAt = created_at
        )
    }

    override fun getAllTrips(): Flow<List<Trip>> {
        return queries.getAllTrips()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTripById(id: Long): Flow<Trip?> {
        return queries.getTripById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun insertTrip(trip: Trip) {
        queries.insertTrip(
            destination = trip.destination,
            start_date = trip.startDate,
            end_date = trip.endDate,
            budget = trip.budget,
            created_at = trip.createdAt
        )
    }

    override suspend fun updateTrip(trip: Trip) {
        queries.updateTrip(
            destination = trip.destination,
            start_date = trip.startDate,
            end_date = trip.endDate,
            budget = trip.budget,
            id = trip.id
        )
    }

    override suspend fun deleteTrip(id: Long) {
        queries.deleteTrip(id)
    }
}