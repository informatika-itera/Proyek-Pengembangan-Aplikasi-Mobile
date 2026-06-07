package com.example.tripmate.data.repository

import app.cash.turbine.test
import com.example.tripmate.domain.model.PackingItem
import com.example.tripmate.domain.repository.PackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PackingRepositoryTest {

    private lateinit var repository: FakePackingRepository

    @BeforeTest
    fun setup() {
        repository = FakePackingRepository()
    }

    @Test
    fun `insertItem should add item to list`() = runTest {
        val item = PackingItem(tripId = 1L, name = "Baju", createdAt = 0L)
        repository.insertItem(item)

        repository.getItemsByTripId(1L).test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Baju", items.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getItemsByTripId should only return items for that trip`() = runTest {
        repository.insertItem(PackingItem(tripId = 1L, name = "Baju", createdAt = 0L))
        repository.insertItem(PackingItem(tripId = 2L, name = "Kamera", createdAt = 0L))

        repository.getItemsByTripId(1L).test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertTrue(items.all { it.tripId == 1L })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateChecked should toggle isChecked status`() = runTest {
        repository.insertItem(PackingItem(tripId = 1L, name = "Passport", createdAt = 0L))

        repository.getItemsByTripId(1L).test {
            val id = awaitItem().first().id
            cancelAndIgnoreRemainingEvents()

            repository.updateChecked(id, true)

            repository.getItemsByTripId(1L).test {
                assertTrue(awaitItem().first().isChecked)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}

class FakePackingRepository : PackingRepository {

    private val items = MutableStateFlow<List<PackingItem>>(emptyList())
    private var nextId = 1L

    override fun getItemsByTripId(tripId: Long): Flow<List<PackingItem>> =
        items.map { list -> list.filter { it.tripId == tripId } }

    override suspend fun insertItem(item: PackingItem) {
        items.update { it + item.copy(id = nextId++) }
    }

    override suspend fun updateChecked(id: Long, isChecked: Boolean) {
        items.update { list -> list.map { if (it.id == id) it.copy(isChecked = isChecked) else it } }
    }

    override suspend fun deleteItem(id: Long) {
        items.update { list -> list.filter { it.id != id } }
    }
}
