package com.example.neurodeck.presentation.screens.decklibrary

import app.cash.turbine.test
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.fakes.FakeDeckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DeckLibraryViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var deckRepo: FakeDeckRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        deckRepo = FakeDeckRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun deck(id: Long, title: String, desc: String = "") =
        Deck(id = id, title = title, description = desc)

    @Test
    fun `Empty state ketika tidak ada deck`() = runTest {
        deckRepo.setDecks(emptyList())
        val vm = DeckLibraryViewModel(deckRepo)

        vm.uiState.test {
            var state = awaitItem()
            while (state is DeckLibraryUiState.Loading) state = awaitItem()
            assertIs<DeckLibraryUiState.Empty>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Success state menampilkan semua deck ketika query kosong`() = runTest {
        deckRepo.setDecks(listOf(deck(1, "Kalkulus"), deck(2, "Fisika")))
        val vm = DeckLibraryViewModel(deckRepo)

        vm.uiState.test {
            var state = awaitItem()
            while (state is DeckLibraryUiState.Loading) state = awaitItem()
            assertIs<DeckLibraryUiState.Success>(state)
            assertEquals(2, state.decks.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search memfilter deck berdasarkan judul`() = runTest {
        deckRepo.setDecks(listOf(deck(1, "Kalkulus"), deck(2, "Fisika Dasar")))
        val vm = DeckLibraryViewModel(deckRepo)

        vm.uiState.test {
            // skip sampai state non-loading awal
            var state = awaitItem()
            while (state is DeckLibraryUiState.Loading) state = awaitItem()
            assertIs<DeckLibraryUiState.Success>(state)

            vm.onSearchQueryChange("fisika")

            var filtered = awaitItem()
            while (filtered is DeckLibraryUiState.Loading) filtered = awaitItem()
            assertIs<DeckLibraryUiState.Success>(filtered)
            assertEquals(1, filtered.decks.size)
            assertEquals("Fisika Dasar", filtered.decks.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search yang tidak match menampilkan NoSearchResults`() = runTest {
        deckRepo.setDecks(listOf(deck(1, "Kalkulus")))
        val vm = DeckLibraryViewModel(deckRepo)

        vm.uiState.test {
            var state = awaitItem()
            while (state is DeckLibraryUiState.Loading) state = awaitItem()

            vm.onSearchQueryChange("xyz tidak ada")

            var result = awaitItem()
            while (result !is DeckLibraryUiState.NoSearchResults) result = awaitItem()
            assertEquals("xyz tidak ada", result.query)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Error state ketika observeAllDecks gagal`() = runTest {
        deckRepo.observeError = RuntimeException("DB error")
        val vm = DeckLibraryViewModel(deckRepo)

        vm.uiState.test {
            var state = awaitItem()
            while (state is DeckLibraryUiState.Loading) state = awaitItem()
            assertIs<DeckLibraryUiState.Error>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createDeck memanggil repository dan onSuccess`() = runTest {
        val vm = DeckLibraryViewModel(deckRepo)
        var newId: Long? = null
        vm.createDeck("Deck Baru", "deskripsi") { newId = it }

        assertTrue(newId != null)
        assertEquals("Deck Baru", deckRepo.lastCreatedTitle)
    }

    @Test
    fun `createDeck dengan judul kosong di-skip`() = runTest {
        val vm = DeckLibraryViewModel(deckRepo)
        var called = false
        vm.createDeck("   ", onSuccess = { called = true })

        assertTrue(!called)
        assertEquals(0, deckRepo.createCallCount)
    }

    @Test
    fun `deleteDeck memanggil repository`() = runTest {
        deckRepo.setDecks(listOf(deck(9, "Hapus aku")))
        val vm = DeckLibraryViewModel(deckRepo)

        vm.deleteDeck(9L)

        assertTrue(deckRepo.deletedIds.contains(9L))
    }

    @Test
    fun `clearSearch mengosongkan query`() = runTest {
        val vm = DeckLibraryViewModel(deckRepo)
        vm.onSearchQueryChange("sesuatu")
        assertEquals("sesuatu", vm.searchQuery.value)

        vm.clearSearch()
        assertEquals("", vm.searchQuery.value)
    }
}
