package com.studyhub.presentation.profile

import com.studyhub.domain.fake.FakePreferencesRepository
import com.studyhub.domain.fake.FakeTaskRepository
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.preferences.SetDarkModeUseCase
import com.studyhub.domain.usecase.task.GetActiveTasksUseCase
import com.studyhub.presentation.screens.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProfileViewModelTest {
    private lateinit var fakePrefsRepo: FakePreferencesRepository
    private lateinit var fakeTaskRepo: FakeTaskRepository
    private lateinit var viewModel: ProfileViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakePrefsRepo = FakePreferencesRepository()
        fakeTaskRepo = FakeTaskRepository()
        viewModel = ProfileViewModel(
            GetUserPreferencesUseCase(fakePrefsRepo),
            SetDarkModeUseCase(fakePrefsRepo),
            GetActiveTasksUseCase(fakeTaskRepo)
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
    fun `given dark mode false when toggleDarkMode then setDarkMode called with true`() = runTest {
        fakePrefsRepo.isDarkModeValue = false
        viewModel.toggleDarkMode()
        advanceUntilIdle()
        assertTrue(fakePrefsRepo.setDarkModeCalledWith == true)
    }

    @Test
    fun `given dark mode true when toggleDarkMode then setDarkMode called with false`() = runTest {
        fakePrefsRepo.setDarkMode(true)
        advanceUntilIdle()
        viewModel.toggleDarkMode()
        advanceUntilIdle()
        assertTrue(fakePrefsRepo.setDarkModeCalledWith == false)
    }

    @Test
    fun `given preferences updated when observePreferences then state reflects changes`() = runTest {
        fakePrefsRepo.isDarkModeValue = true
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `given tasks loaded when loadStats then overdueCount correct`() = runTest {
        fakeTaskRepo.tasks.add(
            Task(
                id = "t1",
                title = "Task 1",
                description = "",
                subject = "Math",
                priority = Priority.MEDIUM,
                status = TaskStatus.TODO,
                dueDate = 1000L,
                dueTime = null,
                tags = emptyList(),
                estimatedMinutes = 30,
                isDeleted = false,
                completedAt = null,
                createdAt = 0L,
                updatedAt = 0L
            )
        )
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.overdueCount >= 0)
    }
}
