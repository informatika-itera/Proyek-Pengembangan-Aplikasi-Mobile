package com.example.edumate.presentation.screens.home

import app.cash.turbine.test
import com.example.edumate.data.repository.FakeTaskRepository
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.model.TaskPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

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
    fun initialState_shouldBeLoading() = runTest {
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun whenTasksLoaded_shouldShowSuccess() = runTest {
        val testTask = Task(
            id = 1,
            title = "Tugas PAM",
            description = "Menyelesaikan Sprint 4",
            priority = TaskPriority.HIGH
        )
        repository.emitTasks(listOf(testTask))

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Loading) {
                val nextState = awaitItem()
                assertTrue(nextState is HomeUiState.Success)
                assertEquals(1, nextState.tasks.size)
                assertEquals("Tugas PAM", nextState.tasks.first().title)
            } else {
                assertTrue(state is HomeUiState.Success)
                assertEquals(1, state.tasks.size)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenListEmpty_shouldShowEmptyState() = runTest {
        repository.emitTasks(emptyList())

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Loading) {
                val nextState = awaitItem()
                assertTrue(nextState is HomeUiState.Empty)
            } else {
                assertTrue(state is HomeUiState.Empty)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchQuery_shouldFilterTasksCorrectly() = runTest {
        val task1 = Task(id = 1, title = "Belajar Kotlin")
        val task2 = Task(id = 2, title = "Mengerjakan Laporan")
        repository.emitTasks(listOf(task1, task2))

        viewModel.updateSearchQuery("Kotlin")

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Loading) {
                val nextState = awaitItem()
                assertTrue(nextState is HomeUiState.Success)
                assertEquals(1, nextState.tasks.size)
                assertEquals("Belajar Kotlin", nextState.tasks.first().title)
            } else {
                assertTrue(state is HomeUiState.Success)
                assertEquals(1, state.tasks.size)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun filterToggle_shouldShowOnlyCompletedTasks() = runTest {
        val task1 = Task(id = 1, title = "Task 1", isCompleted = false)
        val task2 = Task(id = 2, title = "Task 2", isCompleted = true)
        repository.emitTasks(listOf(task1, task2))

        viewModel.updateFilter(TaskFilter.COMPLETED)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Loading) {
                val nextState = awaitItem()
                assertTrue(nextState is HomeUiState.Success)
                assertEquals(1, nextState.tasks.size)
                assertEquals("Task 2", nextState.tasks.first().title)
            } else {
                assertTrue(state is HomeUiState.Success)
                assertEquals(1, state.tasks.size)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}