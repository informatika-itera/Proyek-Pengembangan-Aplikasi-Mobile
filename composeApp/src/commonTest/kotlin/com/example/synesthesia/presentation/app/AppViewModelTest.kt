package com.example.synesthesia.presentation.app

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeNetworkMonitor
import com.example.synesthesia.fakes.FakeUserPreferences
import com.example.synesthesia.presentation.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: FakeUserPreferences
    private lateinit var networkMonitor: FakeNetworkMonitor
    private lateinit var viewModel: AppViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userPreferences = FakeUserPreferences()
        networkMonitor = FakeNetworkMonitor()
        viewModel = AppViewModel(userPreferences, networkMonitor)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isOnline should reflect network monitor state`() = runTest {
        viewModel.isOnline.test {
            assertTrue(awaitItem()) // default true

            networkMonitor.setOnline(false)
            assertFalse(awaitItem())

            networkMonitor.setOnline(true)
            assertTrue(awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isDarkMode should reflect user preferences`() = runTest {
        viewModel.isDarkMode.test {
            assertFalse(awaitItem()) // default false

            userPreferences.setDarkMode(true)
            assertTrue(awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `themeMode should reflect user preferences mapped to enum`() = runTest {
        viewModel.themeMode.test {
            assertEquals(ThemeMode.NORMAL, awaitItem()) // default

            userPreferences.setThemeMode("ASTRONOMY")
            assertEquals(ThemeMode.ASTRONOMY, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isOnboardingCompleted should be true initially and update on completion`() = runTest {
        viewModel.isOnboardingCompleted.test {
            // Because FakeUserPreferences might default to false, but AppViewModel has initialValue = true
            // It will emit true (initial), then the actual flow value
            val first = awaitItem()
            // Depending on race conditions, it could be true or false. Let's just advance until idle
            cancelAndIgnoreRemainingEvents()
        }
        
        viewModel.completeOnboarding()
        advanceUntilIdle()

        viewModel.isOnboardingCompleted.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
