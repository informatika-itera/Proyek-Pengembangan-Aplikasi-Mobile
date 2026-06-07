package com.example.bridgebit.data.repository

import com.example.bridgebit.data.local.BridgeBitDatabase
import com.example.bridgebit.data.local.BridgeBitQueries
import com.example.bridgebit.domain.model.Translation
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TranslationRepositoryImplTest {

    private lateinit var database: BridgeBitDatabase
    private lateinit var queries: BridgeBitQueries
    private lateinit var repository: TranslationRepositoryImpl

    @Before
    fun setup() {
        database = mockk()
        queries = mockk(relaxed = true)

        // Memalsukan objek queries agar database tidak error saat dites
        every { database.bridgeBitQueries } returns queries
        repository = TranslationRepositoryImpl(database)
    }

    @Test
    fun `Test 27 - insertTranslation should map to entity and execute insert`() = runTest {
        val translation = Translation(0L, "Kucing", "Cat", "ID", "EN", "Hewan", true, 1000L, 1000L)

        // Mock eksekusi ID terakhir dari SqlDelight
        val queryMock = mockk<app.cash.sqldelight.ExecutableQuery<Long>>(relaxed = true)
        every { queryMock.executeAsOne() } returns 99L
        every { queries.lastInsertId() } returns queryMock

        val resultId = repository.insertTranslation(translation)

        assertEquals(99L, resultId)

        // Pastikan mapping berjalan (isVaulted = true menjadi 1L)
        coVerify {
            queries.insertTranslation(
                source_text = "Kucing",
                translated_text = "Cat",
                source_language = "ID",
                target_language = "EN",
                category = "Hewan",
                is_vaulted = 1L,
                created_at = 1000L,
                updated_at = 1000L
            )
        }
    }

    @Test
    fun `Test 28 - updateTranslation should map fields and update timestamp`() = runTest {
        val translation = Translation(5L, "Buku", "Book", "ID", "EN", "Edukasi", false, 1000L, 1000L)

        repository.updateTranslation(translation)

        // Pastikan isVaulted = false diubah menjadi 0L
        coVerify {
            queries.updateTranslation(
                id = 5L,
                source_text = "Buku",
                translated_text = "Book",
                source_language = "ID",
                target_language = "EN",
                category = "Edukasi",
                is_vaulted = 0L,
                updated_at = any()
            )
        }
    }

    @Test
    fun `Test 29 - deleteTranslationById should execute delete query`() = runTest {
        repository.deleteTranslationById(12L)

        coVerify(exactly = 1) { queries.deleteTranslationById(12L) }
    }

    @Test
    fun `Test 30 - toggleVaultStatus should execute toggle query`() = runTest {
        repository.toggleVaultStatus(7L)

        // Perbaikan: gunakan nama parameter eksplisit 'id = eq(7L)' dan 'updated_at = any()'
        coVerify(exactly = 1) {
            queries.toggleVaultStatus(
                id = eq(7L),
                updated_at = any()
            )
        }
    }

    @Test
    fun `Test 31 - deleteTranslationsByIds should execute bulk delete query`() = runTest {
        val idsToDelete = listOf(1L, 2L, 3L)
        repository.deleteTranslationsByIds(idsToDelete)

        coVerify(exactly = 1) { queries.deleteTranslationsByIds(idsToDelete) }
    }
}