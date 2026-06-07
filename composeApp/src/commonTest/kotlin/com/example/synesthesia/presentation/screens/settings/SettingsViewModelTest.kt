package com.example.synesthesia.presentation.screens.settings

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeUserPreferences
import com.example.synesthesia.presentation.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * SettingsViewModel Unit Tests
 * Targets 100% logic coverage for theme and dark mode toggles.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: FakeUserPreferences
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userPreferences = FakeUserPreferences()
        viewModel = SettingsViewModel(userPreferences)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isDarkMode should emit values from preferences`() = runTest {
        viewModel.isDarkMode.test {
            // Initial value (FakeUserPreferences defaults to false)
            assertEquals(false, awaitItem())
            
            viewModel.toggleDarkMode(true)
            assertEquals(true, awaitItem())
            
            viewModel.toggleDarkMode(false)
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `themeMode should emit values from preferences`() = runTest {
        viewModel.themeMode.test {
            // Initial value (FakeUserPreferences defaults to NORMAL)
            assertEquals(ThemeMode.NORMAL, awaitItem())
            
            viewModel.setThemeMode(ThemeMode.ASTRONOMY)
            assertEquals(ThemeMode.ASTRONOMY, awaitItem())
            
            viewModel.setThemeMode(ThemeMode.NORMAL)
            assertEquals(ThemeMode.NORMAL, awaitItem())
        }
    }
}
