package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.data.local.TravelPlannerDatabase
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class TripRepositoryImpl(
    private val database: TravelPlannerDatabase
) : TripRepository {

    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }
    private val queries = database.travelDatabaseQueries

    override fun getAllTrips(): Flow<List<Trip>> {
        return queries.getAllTrips()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    Trip(
                        id = entity.id,
                        destination = entity.city,
                        startDate = entity.start_date,
                        endDate = entity.start_date,
                        duration = entity.duration,
                        vibe = entity.vibes,
                        itineraryItems = try {
                            jsonParser.decodeFromString<List<ItineraryItem>>(entity.itinerary_json)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    )
                }
            }
    }

    override fun getTripById(id: String): Flow<Trip?> {
        return queries.getTripById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity ->
                entity?.let {
                    Trip(
                        id = it.id,
                        destination = it.city,
                        startDate = it.start_date,
                        endDate = it.start_date,
                        duration = it.duration,
                        vibe = it.vibes,
                        itineraryItems = try {
                            jsonParser.decodeFromString<List<ItineraryItem>>(it.itinerary_json)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    )
                }
            }
    }

    override suspend fun saveTrip(trip: Trip) {
        val itineraryJson = jsonParser.encodeToString(trip.itineraryItems)
        queries.insertTrip(
            id = trip.id,
            user_id = null,
            city = trip.destination,
            vibes = trip.vibe,
            duration = trip.duration,
            start_date = trip.startDate,
            itinerary_json = itineraryJson,
            created_at = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteTrip(id: String) {
        queries.deleteTrip(id)
    }
}
