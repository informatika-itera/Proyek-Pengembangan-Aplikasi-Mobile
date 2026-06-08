package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.data.local.TravelPlannerDatabase
import com.example.travelplanner.data.local.TravelDatabaseQueries
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class TripRepositoryTest {
    private val database: TravelPlannerDatabase = mockk(relaxed = true)
    private val queries: TravelDatabaseQueries = mockk(relaxed = true)
    private lateinit var repository: TripRepositoryImpl

    @BeforeTest
    fun setUp() {
        every { database.travelDatabaseQueries } returns queries
        repository = TripRepositoryImpl(database)
    }

    @Test
    fun testSaveTrip() = runTest {
        val trip = Trip(
            id = "trip_123",
            destination = "Bali",
            startDate = "1 Jun 2026",
            endDate = "5 Jun 2026",
            duration = "Jakarta|1 Jun 2026 – 5 Jun 2026",
            vibe = "Beach",
            itineraryItems = listOf(
                ItineraryItem(time = "09:00", activity = "Swim", icon = "🏊")
            )
        )

        coEvery { queries.insertTrip(any(), any(), any(), any(), any(), any(), any(), any()) } just runs

        repository.saveTrip(trip)

        coVerify {
            queries.insertTrip(
                id = "trip_123",
                user_id = null,
                city = "Bali",
                vibes = "Beach",
                duration = "Jakarta|1 Jun 2026 – 5 Jun 2026",
                start_date = "1 Jun 2026",
                itinerary_json = any(),
                created_at = any()
            )
        }
    }

    @Test
    fun testDeleteTrip() = runTest {
        coEvery { queries.deleteTrip(any()) } just runs
        repository.deleteTrip("trip_123")
        coVerify { queries.deleteTrip("trip_123") }
    }
}
