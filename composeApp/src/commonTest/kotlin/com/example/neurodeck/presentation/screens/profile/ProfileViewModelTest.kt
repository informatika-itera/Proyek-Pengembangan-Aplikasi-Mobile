package com.example.neurodeck.presentation.screens.profile

import app.cash.turbine.test
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.reminder.NoOpReminderScheduler
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
import kotlin.test.assertNotNull

class ProfileViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var prefsRepo: FakeUserPreferencesRepository
    private lateinit var deckRepo: FakeDeckRepository
    private lateinit var reviewRepo: FakeReviewRecordRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        prefsRepo = FakeUserPreferencesRepository()
        deckRepo = FakeDeckRepository()
        reviewRepo = FakeReviewRecordRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun newVm() = ProfileViewModel(prefsRepo, deckRepo, reviewRepo, NoOpReminderScheduler())

    @Test
    fun `state menggabungkan profil tema dan achievement`() = runTest {
        prefsRepo.profileFlow.value = UserProfile(name = "Fajri")
        prefsRepo.themeModeFlow.value = ThemeMode.Dark
        deckRepo.setDecks(listOf(Deck(id = 1, title = "A", cardCount = 10)))
        reviewRepo.totalReviews = 99
        reviewRepo.streakDays = 7

        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()

            assertEquals("Fajri", state.profile.name)
            assertEquals(ThemeMode.Dark, state.themeMode)
            assertEquals(1, state.totalDecks)
            assertEquals(10, state.totalCards)
            assertEquals(99, state.totalReviews)
            assertEquals(7, state.streakDays)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setThemeMode mempersist tema baru`() = runTest {
        val vm = newVm()
        vm.setThemeMode(ThemeMode.Light)
        assertEquals(ThemeMode.Light, prefsRepo.lastThemeSet)
    }

    @Test
    fun `resetAllData memanggil resetPreferences dan menampilkan snackbar`() = runTest {
        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()

            vm.resetAllData()
            var afterReset = awaitItem()
            while (afterReset.snackbarMessage == null) afterReset = awaitItem()
            assertNotNull(afterReset.snackbarMessage)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(true, prefsRepo.resetCalled)
    }

    @Test
    fun `setThemeMode gagal menampilkan snackbar error`() = runTest {
        prefsRepo.throwOnSetTheme = true
        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()

            vm.setThemeMode(ThemeMode.Dark)
            var afterErr = awaitItem()
            while (afterErr.snackbarMessage == null) afterErr = awaitItem()
            assertNotNull(afterErr.snackbarMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `consumeSnackbar menghapus pesan`() = runTest {
        val vm = newVm()
        vm.uiState.test {
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()

            vm.resetAllData()
            var withMsg = awaitItem()
            while (withMsg.snackbarMessage == null) withMsg = awaitItem()
            assertNotNull(withMsg.snackbarMessage)

            vm.consumeSnackbar()
            var cleared = awaitItem()
            while (cleared.snackbarMessage != null) cleared = awaitItem()
            assertEquals(null, cleared.snackbarMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }
}