package com.studyhub.presentation.profile

import com.studyhub.domain.fake.FakePreferencesRepository
import com.studyhub.domain.fake.FakeReminderRepository
import com.studyhub.domain.fake.FakeTaskRepository
import com.studyhub.domain.fake.TaskBuilder
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.preferences.SetDarkModeUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.presentation.screens.profile.ProfileUiState
import com.studyhub.presentation.screens.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private lateinit var fakeTaskRepo: FakeTaskRepository
    private lateinit var fakePrefsRepo: FakePreferencesRepository
    private lateinit var fakeReminderRepo: FakeReminderRepository
    private lateinit var viewModel: ProfileViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeTaskRepo = FakeTaskRepository()
        fakePrefsRepo = FakePreferencesRepository()
        fakeReminderRepo = FakeReminderRepository()
        
        viewModel = ProfileViewModel(
            GetUserPreferencesUseCase(fakePrefsRepo),
            SetDarkModeUseCase(fakePrefsRepo),
            GetAllTasksUseCase(fakeTaskRepo),
            fakePrefsRepo,
            fakeReminderRepo
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given tasks when created then state is Success with counts`() = runTest {
        val collectJob = launch { viewModel.uiState.collect() }
        
        fakeTaskRepo.addTasks(listOf(
            TaskBuilder.build(id = "1", status = TaskStatus.DONE),
            TaskBuilder.build(id = "2", status = TaskStatus.TODO)
        ))
        
        val state = viewModel.uiState.value as? ProfileUiState.Success
        assertNotNull(state)
        assertEquals(2, state.totalTasks)
        assertEquals(1, state.doneTasks)
        assertEquals(50, state.completionRate)
        
        collectJob.cancel()
    }

    @Test
    fun `given no tasks when created then completionRate is 0`() = runTest {
        val collectJob = launch { viewModel.uiState.collect() }
        
        val state = viewModel.uiState.value as? ProfileUiState.Success
        assertNotNull(state)
        assertEquals(0, state.completionRate)
        
        collectJob.cancel()
    }

    @Test
    fun `when toggleDarkMode then prefs updated`() = runTest {
        val collectJob = launch { viewModel.uiState.collect() }
        
        viewModel.toggleDarkMode()
        assertTrue(fakePrefsRepo.isDarkModeValue)
        
        collectJob.cancel()
    }
}
