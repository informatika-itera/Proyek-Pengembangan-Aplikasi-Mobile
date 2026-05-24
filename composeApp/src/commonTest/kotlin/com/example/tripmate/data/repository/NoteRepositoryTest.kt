package com.example.tripmate.data.repository

import app.cash.turbine.test
import com.example.tripmate.domain.model.Trip
import com.example.tripmate.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TripRepositoryTest {

    private lateinit var repository: FakeTripRepository

    @BeforeTest
    fun setup() {
        repository = FakeTripRepository()
    }

    @Test
    fun `insertTrip should add trip to list`() = runTest {
        val trip = createTestTrip(destination = "Bali")

        repository.insertTrip(trip)

        repository.getAllTrips().test {
            val trips = awaitItem()
            assertEquals(1, trips.size)
            assertEquals("Bali", trips.first().destination)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllTrips should return all trips`() = runTest {
        repository.insertTrip(createTestTrip(destination = "Bali"))
        repository.insertTrip(createTestTrip(destination = "Lombok"))

        repository.getAllTrips().test {
            val trips = awaitItem()
            assertEquals(2, trips.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTripById should return correct trip`() = runTest {
        repository.insertTrip(createTestTrip(destination = "Yogyakarta"))

        repository.getAllTrips().test {
            val trips = awaitItem()
            val id = trips.first().id

            cancelAndIgnoreRemainingEvents()

            repository.getTripById(id).test {
                val trip = awaitItem()
                assertNotNull(trip)
                assertEquals("Yogyakarta", trip.destination)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `getTripById should return null for non-existent id`() = runTest {
        repository.getTripById(999L).test {
            val trip = awaitItem()
            assertEquals(null, trip)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTrip should remove trip from list`() = runTest {
        repository.insertTrip(createTestTrip(destination = "Bandung"))

        repository.getAllTrips().test {
            val trips = awaitItem()
            val id = trips.first().id
            cancelAndIgnoreRemainingEvents()

            repository.deleteTrip(id)

            repository.getAllTrips().test {
                val remaining = awaitItem()
                assertTrue(remaining.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `updateTrip should modify existing trip`() = runTest {
        repository.insertTrip(createTestTrip(destination = "Jakarta"))

        repository.getAllTrips().test {
            val trips = awaitItem()
            val trip = trips.first()
            cancelAndIgnoreRemainingEvents()

            repository.updateTrip(trip.copy(destination = "Surabaya"))

            repository.getTripById(trip.id).test {
                val updated = awaitItem()
                assertEquals("Surabaya", updated?.destination)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    private fun createTestTrip(destination: String = "Test"): Trip {
        return Trip(
            id = 0,
            destination = destination,
            startDate = "2024-01-01",
            endDate = "2024-01-07",
            budget = 1000000.0,
            createdAt = System.currentTimeMillis()
        )
    }
}

class FakeTripRepository : TripRepository {

    private val trips = MutableStateFlow<List<Trip>>(emptyList())
    private var nextId = 1L

    override fun getAllTrips(): Flow<List<Trip>> = trips

    override fun getTripById(id: Long): Flow<Trip?> {
        return trips.map { list -> list.find { it.id == id } }
    }

    override suspend fun insertTrip(trip: Trip) {
        val newTrip = trip.copy(id = nextId++)
        trips.update { it + newTrip }
    }

    override suspend fun updateTrip(trip: Trip) {
        trips.update { list ->
            list.map { if (it.id == trip.id) trip else it }
        }
    }

    override suspend fun deleteTrip(id: Long) {
        trips.update { list -> list.filter { it.id != id } }
    }
}
