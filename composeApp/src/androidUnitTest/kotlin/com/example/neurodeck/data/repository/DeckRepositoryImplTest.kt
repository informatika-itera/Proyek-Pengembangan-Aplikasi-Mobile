package com.example.neurodeck.data.repository

import app.cash.sqldelight.db.SqlDriver
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.local.TestDatabaseFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Integration test untuk [DeckRepositoryImpl] memakai SQLDelight in-memory.
 *
 * Test ini menjalankan query SQL beneran (insert/select/update/delete) sehingga
 * memverifikasi mapping entity↔domain, transaction, dan agregasi cardCount.
 */
class DeckRepositoryImplTest {

    private lateinit var driver: SqlDriver
    private lateinit var database: NeuroDeckDatabase
    private lateinit var repository: DeckRepositoryImpl

    @BeforeTest
    fun setup() {
        driver = TestDatabaseFactory.createDriver()
        database = TestDatabaseFactory.create(driver)
        repository = DeckRepositoryImpl(database)
    }

    @AfterTest
    fun teardown() {
        driver.close()
    }

    @Test
    fun `createDeck menyimpan dan mengembalikan id valid`() = runTest {
        val id = repository.createDeck("Kalkulus", "Materi turunan")
        assertTrue(id > 0, "id hasil insert harus positif")

        val decks = repository.observeAllDecks().first()
        assertEquals(1, decks.size)
        assertEquals("Kalkulus", decks.first().title)
        assertEquals("Materi turunan", decks.first().description)
    }

    @Test
    fun `observeAllDecks mengembalikan kosong saat belum ada deck`() = runTest {
        assertTrue(repository.observeAllDecks().first().isEmpty())
    }

    @Test
    fun `observeDeckById mengembalikan deck yang sesuai`() = runTest {
        val id = repository.createDeck("Fisika", "")
        val deck = repository.observeDeckById(id).first()
        assertEquals(id, deck?.id)
        assertEquals("Fisika", deck?.title)
    }

    @Test
    fun `observeDeckById mengembalikan null untuk id tidak ada`() = runTest {
        assertNull(repository.observeDeckById(999L).first())
    }

    @Test
    fun `updateDeck mengubah title dan description`() = runTest {
        val id = repository.createDeck("Lama", "deskripsi lama")
        val deck = repository.observeDeckById(id).first()!!

        repository.updateDeck(deck.copy(title = "Baru", description = "deskripsi baru"))

        val updated = repository.observeDeckById(id).first()!!
        assertEquals("Baru", updated.title)
        assertEquals("deskripsi baru", updated.description)
    }

    @Test
    fun `deleteDeck menghapus deck`() = runTest {
        val id = repository.createDeck("Hapus", "")
        repository.deleteDeck(id)
        assertTrue(repository.observeAllDecks().first().isEmpty())
    }

    @Test
    fun `cardCount terhitung benar via join`() = runTest {
        val deckId = repository.createDeck("Dengan Kartu", "")
        val cardRepo = CardRepositoryImpl(
            database,
            com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase(),
        )
        cardRepo.createCard(deckId, "f1", "b1")
        cardRepo.createCard(deckId, "f2", "b2")

        val deck = repository.observeAllDecks().first().first { it.id == deckId }
        assertEquals(2, deck.cardCount, "cardCount harus mengikuti jumlah kartu")
    }

    @Test
    fun `deck diurutkan terbaru di atas`() = runTest {
        val first = repository.createDeck("Pertama", "")
        val second = repository.createDeck("Kedua", "")
        // Update first supaya updatedAt-nya jadi paling baru
        val firstDeck = repository.observeDeckById(first).first()!!
        repository.updateDeck(firstDeck.copy(title = "Pertama (diupdate)"))

        val decks = repository.observeAllDecks().first()
        assertEquals(first, decks.first().id, "Deck yang baru diupdate harus di atas")
        assertEquals(second, decks[1].id)
    }
}
