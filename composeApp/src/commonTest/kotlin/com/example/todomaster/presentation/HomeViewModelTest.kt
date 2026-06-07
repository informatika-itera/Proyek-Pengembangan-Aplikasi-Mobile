package com.example.todomaster.presentation

import app.cash.turbine.test
import com.example.todomaster.data.repository.FakeTaskRepository
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        viewModel = HomeViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty task lists`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.doFirstTasks.isEmpty())
            assertTrue(initialState.scheduleTasks.isEmpty())
            assertTrue(initialState.delegateTasks.isEmpty())
            assertTrue(initialState.dontDoTasks.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should sort tasks into correct quadrants`() = runTest {
        // Arrange
        repository.insertTask(createTestTask("Task 1", Quadrant.DO_FIRST))
        repository.insertTask(createTestTask("Task 2", Quadrant.SCHEDULE))

        val vm = HomeViewModel(repository)
        advanceUntilIdle()

        // Act & Assert
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.doFirstTasks.size)
            assertEquals("Task 1", state.doFirstTasks.first().title)
            assertEquals(1, state.scheduleTasks.size)
            assertEquals("Task 2", state.scheduleTasks.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query change should update search query state`() = runTest {
        // Act
        viewModel.onSearchQueryChange("Belajar PAM")

        // Assert
        viewModel.searchQuery.test {
            assertEquals("Belajar PAM", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changeTab should update current tab index`() = runTest {
        // Act
        viewModel.changeTab(2)

        // Assert
        viewModel.currentTab.test {
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleTaskCompletion should invoke repository updates`() = runTest {
        // Arrange
        val task = createTestTask("Task Testing", Quadrant.DO_FIRST).copy(id = 1)
        repository.insertTask(task)

        // Act
        viewModel.toggleTaskCompletion(task)
        advanceUntilIdle()

        // Assert
        val updatedTask = repository.getTaskById(1)
        assertTrue(updatedTask?.isCompleted == true)
    }

    private fun createTestTask(title: String, priority: Quadrant): Task {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return Task(
            id = 0,
            title = title,
            description = "Test Description",
            priority = priority,
            dueDate = currentTime,
            isCompleted = false,
            parentTaskTitle = null,
            createdAt = currentTime
        )
    }
}