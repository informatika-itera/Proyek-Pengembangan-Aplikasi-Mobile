package com.example.neurodeck.presentation.screens.stats

import app.cash.turbine.test
import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.CardReviewState
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.fakes.FakeCardRepository
import com.example.neurodeck.fakes.FakeDeckRepository
import com.example.neurodeck.fakes.FakeReviewRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class StatsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var deckRepo: FakeDeckRepository
    private lateinit var cardRepo: FakeCardRepository
    private lateinit var reviewRepo: FakeReviewRecordRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        deckRepo = FakeDeckRepository()
        cardRepo = FakeCardRepository()
        reviewRepo = FakeReviewRecordRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun newVm() = StatsViewModel(deckRepo, cardRepo, reviewRepo)

    private fun cardWith(id: Long, deckId: Long, repetitions: Int, intervalDays: Int) =
        Card(
            id = id,
            deckId = deckId,
            front = "f",
            back = "b",
            reviewState = CardReviewState(repetitions = repetitions, intervalDays = intervalDays),
        )

    @Test
    fun `period default adalah Week`() = runTest {
        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state is StatsUiState.Loading) state = awaitItem()
            assertIs<StatsUiState.Success>(state)
            assertEquals(StatsPeriod.Week, state.period)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Success state mengisi metrik dasar`() = runTest {
        deckRepo.setDecks(listOf(Deck(id = 1, title = "A", cardCount = 3)))
        reviewRepo.reviewsInRange = 20
        reviewRepo.streakDays = 4
        reviewRepo.accuracyInRange = 0.75 // 75%

        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state is StatsUiState.Loading) state = awaitItem()
            assertIs<StatsUiState.Success>(state)
            assertEquals(20, state.totalReviews)
            assertEquals(4, state.streakDays)
            assertEquals(1, state.totalDecks)
            assertEquals(3, state.totalCards)
            assertEquals(75.0, state.accuracyPercent)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `breakdown status kartu mengklasifikasikan New Learning Mastered`() = runTest {
        deckRepo.setDecks(listOf(Deck(id = 1, title = "A")))
        cardRepo.cardsFlow.value = listOf(
            cardWith(1, 1, repetitions = 0, intervalDays = 0),   // New
            cardWith(2, 1, repetitions = 2, intervalDays = 6),   // Learning (interval < 21)
            cardWith(3, 1, repetitions = 5, intervalDays = 30),  // Mastered (interval >= 21)
        )

        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state is StatsUiState.Loading) state = awaitItem()
            assertIs<StatsUiState.Success>(state)
            val breakdown = state.cardsByStatus
            assertEquals(1, breakdown.newCount)
            assertEquals(1, breakdown.learningCount)
            assertEquals(1, breakdown.masteredCount)
            assertEquals(3, breakdown.total)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changePeriod ke AllTime memperbarui state`() = runTest {
        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state is StatsUiState.Loading) state = awaitItem()
            assertIs<StatsUiState.Success>(state)

            vm.changePeriod(StatsPeriod.AllTime)

            var updated = awaitItem()
            while (updated !is StatsUiState.Success || updated.period != StatsPeriod.AllTime) {
                updated = awaitItem()
            }
            assertEquals(StatsPeriod.AllTime, updated.period)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Error state ketika repository gagal`() = runTest {
        deckRepo.setDecks(listOf(Deck(id = 1, title = "A")))
        reviewRepo.throwOnStreak = true
        val vm = newVm()

        vm.uiState.test {
            var state = awaitItem()
            while (state is StatsUiState.Loading) state = awaitItem()
            assertIs<StatsUiState.Error>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
