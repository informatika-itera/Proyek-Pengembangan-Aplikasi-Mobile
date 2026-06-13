package com.example.neurodeck.presentation.screens.home

import app.cash.turbine.test
import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.fakes.FakeCardRepository
import com.example.neurodeck.fakes.FakeDeckRepository
import com.example.neurodeck.fakes.FakeReviewRecordRepository
import com.example.neurodeck.fakes.FakeUserPreferencesRepository
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

class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var deckRepo: FakeDeckRepository
    private lateinit var cardRepo: FakeCardRepository
    private lateinit var reviewRepo: FakeReviewRecordRepository
    private lateinit var prefsRepo: FakeUserPreferencesRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        deckRepo = FakeDeckRepository()
        cardRepo = FakeCardRepository()
        reviewRepo = FakeReviewRecordRepository()
        prefsRepo = FakeUserPreferencesRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun newVm() = HomeViewModel(deckRepo, cardRepo, reviewRepo, prefsRepo)

    @Test
    fun `Success state menggabungkan stats dari semua repository`() = runTest {
        prefsRepo.profileFlow.value = UserProfile(name = "Fajri")
        deckRepo.setDecks(listOf(Deck(id = 1, title = "Kalkulus")))
        cardRepo.allDueCount = 12L
        reviewRepo.streakDays = 5
        reviewRepo.reviewedToday = 8

        val vm = newVm()

        vm.uiState.test {
            var state = awaitItem()
            while (state is HomeUiState.Loading) state = awaitItem()

            assertIs<HomeUiState.Success>(state)
            assertEquals("Fajri", state.userName)
            assertEquals(12, state.dueCardsCount)
            assertEquals(5, state.streakDays)
            assertEquals(8, state.reviewedToday)
            assertTrue(state.greeting.isNotBlank())
            assertTrue(state.tipOfTheDay.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recentDecks dibatasi maksimal 3 deck`() = runTest {
        deckRepo.setDecks(
            (1..5L).map { Deck(id = it, title = "Deck $it") },
        )
        val vm = newVm()

        vm.uiState.test {
            var state = awaitItem()
            while (state is HomeUiState.Loading) state = awaitItem()
            assertIs<HomeUiState.Success>(state)
            assertTrue(state.recentDecks.size <= 3, "Maksimal 3 recent decks")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recentDecks menyertakan jumlah kartu due per deck`() = runTest {
        deckRepo.setDecks(listOf(Deck(id = 1, title = "Deck A")))
        cardRepo.dueCards = listOf(
            Card(id = 1, deckId = 1, front = "f", back = "b"),
            Card(id = 2, deckId = 1, front = "f", back = "b"),
        )
        val vm = newVm()

        vm.uiState.test {
            var state = awaitItem()
            while (state is HomeUiState.Loading) state = awaitItem()
            assertIs<HomeUiState.Success>(state)
            assertEquals(2, state.recentDecks.first().dueCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Error state ketika repository melempar exception`() = runTest {
        deckRepo.setDecks(listOf(Deck(id = 1, title = "X")))
        reviewRepo.throwOnStreak = true
        val vm = newVm()

        vm.uiState.test {
            var state = awaitItem()
            while (state is HomeUiState.Loading) state = awaitItem()
            assertIs<HomeUiState.Error>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
