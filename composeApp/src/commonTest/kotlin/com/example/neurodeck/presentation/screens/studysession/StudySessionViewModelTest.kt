package com.example.neurodeck.presentation.screens.studysession

import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.ReviewRating
import com.example.neurodeck.fakes.FakeCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StudySessionViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var cardRepo: FakeCardRepository
    private val deckId = 1L

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        cardRepo = FakeCardRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun card(id: Long) = Card(id = id, deckId = deckId, front = "F$id", back = "B$id")

    @Test
    fun `init menampilkan NoCardsDue kalau tidak ada kartu due`() = runTest {
        cardRepo.dueCards = emptyList()
        val vm = StudySessionViewModel(deckId, cardRepo)
        assertIs<StudySessionUiState.NoCardsDue>(vm.uiState.value)
    }

    @Test
    fun `init menampilkan kartu pertama sisi depan`() = runTest {
        cardRepo.dueCards = listOf(card(1), card(2))
        val vm = StudySessionViewModel(deckId, cardRepo)

        val state = vm.uiState.value
        assertIs<StudySessionUiState.ShowingCard>(state)
        assertEquals(1L, state.currentCard.id)
        assertFalse(state.showingBack, "Mulai dari sisi depan (pertanyaan)")
        assertEquals(2, state.totalCards)
        assertEquals(0, state.cardsReviewed)
    }

    @Test
    fun `init menampilkan Error kalau load gagal`() = runTest {
        cardRepo.observeDueError = RuntimeException("DB down")
        val vm = StudySessionViewModel(deckId, cardRepo)
        assertIs<StudySessionUiState.Error>(vm.uiState.value)
    }

    @Test
    fun `flipCard menampilkan sisi belakang`() = runTest {
        cardRepo.dueCards = listOf(card(1))
        val vm = StudySessionViewModel(deckId, cardRepo)

        vm.flipCard()

        val state = vm.uiState.value
        assertIs<StudySessionUiState.ShowingCard>(state)
        assertTrue(state.showingBack)
    }

    @Test
    fun `rateCard sebelum flip tidak melakukan apa-apa`() = runTest {
        cardRepo.dueCards = listOf(card(1))
        val vm = StudySessionViewModel(deckId, cardRepo)

        // Belum flip → rateCard harus di-ignore (safety)
        vm.rateCard(ReviewRating.GOOD)

        assertTrue(cardRepo.reviewedCards.isEmpty(), "Tidak boleh review tanpa flip dulu")
    }

    @Test
    fun `rateCard setelah flip mencatat review dan lanjut ke kartu berikutnya`() = runTest {
        cardRepo.dueCards = listOf(card(1), card(2))
        val vm = StudySessionViewModel(deckId, cardRepo)

        vm.flipCard()
        vm.rateCard(ReviewRating.GOOD)

        // Review kartu 1 tercatat
        assertEquals(1, cardRepo.reviewedCards.size)
        assertEquals(1L to ReviewRating.GOOD, cardRepo.reviewedCards.first())

        // Lanjut ke kartu kedua, sisi depan lagi
        val state = vm.uiState.value
        assertIs<StudySessionUiState.ShowingCard>(state)
        assertEquals(2L, state.currentCard.id)
        assertFalse(state.showingBack)
        assertEquals(1, state.cardsReviewed)
    }

    @Test
    fun `session selesai setelah semua kartu di-review`() = runTest {
        cardRepo.dueCards = listOf(card(1), card(2))
        val vm = StudySessionViewModel(deckId, cardRepo)

        // Kartu 1
        vm.flipCard()
        vm.rateCard(ReviewRating.GOOD)
        // Kartu 2
        vm.flipCard()
        vm.rateCard(ReviewRating.EASY)

        val state = vm.uiState.value
        assertIs<StudySessionUiState.Completed>(state)
        assertEquals(2, state.totalReviewed)
        assertEquals(2, cardRepo.reviewedCards.size)
    }

    @Test
    fun `rateCard menampilkan Error kalau reviewCard gagal`() = runTest {
        cardRepo.dueCards = listOf(card(1))
        cardRepo.throwOnReview = true
        val vm = StudySessionViewModel(deckId, cardRepo)

        vm.flipCard()
        vm.rateCard(ReviewRating.GOOD)

        assertIs<StudySessionUiState.Error>(vm.uiState.value)
    }
}
