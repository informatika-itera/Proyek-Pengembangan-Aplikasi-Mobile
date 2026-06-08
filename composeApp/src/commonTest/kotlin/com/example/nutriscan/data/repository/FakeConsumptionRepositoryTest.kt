package com.example.nutriscan.data.repository

import app.cash.turbine.test
import com.example.nutriscan.domain.model.ConsumptionEntry
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeConsumptionRepositoryTest {

    private lateinit var repository: FakeConsumptionRepository

    private fun entry(name: String = "Aqua") = ConsumptionEntry(
        productName = name,
        calories = 100f,
        sugar = 5f,
        sodium = 50f
    )

    @BeforeTest
    fun setup() {
        repository = FakeConsumptionRepository()
    }

    @Test
    fun `observeAll awalnya kosong`() = runTest {
        repository.observeAll().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add menambahkan consumption entry`() = runTest {
        repository.add(entry("Nasi"))

        repository.observeAll().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Nasi", list.first().productName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete menghapus entry berdasarkan id`() = runTest {
        repository.add(entry("Roti"))
        val id = repository.currentEntries().first().id

        repository.delete(id)

        assertTrue(repository.currentEntries().isEmpty())
    }

    @Test
    fun `clear menghapus semua entry`() = runTest {
        repository.add(entry("A"))
        repository.add(entry("B"))

        repository.clear()

        assertTrue(repository.currentEntries().isEmpty())
    }
}