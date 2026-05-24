package com.studyhub.presentation.theme

import com.studyhub.domain.fake.FakePreferencesRepository
import com.studyhub.domain.usecase.preferences.GetDarkModeUseCase
import com.studyhub.domain.usecase.preferences.SetDarkModeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThemeViewModelTest {
    private lateinit var fakeRepo: FakePreferencesRepository
    private lateinit var viewModel: ThemeViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepo = FakePreferencesRepository()
        viewModel = ThemeViewModel(
            GetDarkModeUseCase(fakeRepo),
            SetDarkModeUseCase(fakeRepo)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given initial state when created then isDarkMode is false`() = runTest {
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isDarkMode)
    }

    @Test
    fun `given dark mode false when toggleDarkMode then isDarkMode becomes true`() = runTest {
        fakeRepo.isDarkModeValue = false
        viewModel.toggleDarkMode()
        advanceUntilIdle()
        assertTrue(fakeRepo.setDarkModeCalledWith == true)
    }

    @Test
    fun `given dark mode true when toggleDarkMode then isDarkMode becomes false`() = runTest {
        fakeRepo.isDarkModeValue = true
        viewModel.toggleDarkMode()
        advanceUntilIdle()
        assertTrue(fakeRepo.setDarkModeCalledWith == false)
    }

    @Test
    fun `given setDarkMode true when called then repo is updated`() = runTest {
        viewModel.setDarkMode(true)
        advanceUntilIdle()
        assertTrue(fakeRepo.setDarkModeCalledWith == true)
    }

    @Test
    fun `given isLoaded when theme loaded then is true`() = runTest {
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isLoaded)
    }
}
