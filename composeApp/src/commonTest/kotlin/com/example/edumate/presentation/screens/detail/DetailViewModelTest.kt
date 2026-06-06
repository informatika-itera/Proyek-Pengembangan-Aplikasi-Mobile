package com.example.edumate.presentation.screens.detail

import com.example.edumate.data.repository.FakeTaskRepository
import com.example.edumate.domain.model.Task
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private lateinit var repository: FakeTaskRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadExistingTask_shouldUpdateStateWithTask() = runTest {
        val task = Task(id = 1, title = "Task Detail")
        repository.insertTask(task)

        val viewModel = DetailViewModel(repository, 1L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Task Detail", state.task?.title)
        assertNull(state.error)
    }

    @Test
    fun loadNonExistentTask_shouldShowError() = runTest {
        val viewModel = DetailViewModel(repository, 999L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.task)
        assertEquals("Tugas tidak ditemukan", state.error)
    }

    @Test
    fun deleteTask_shouldCallRepositoryAndInvokeCallback() = runTest {
        val task = Task(id = 1, title = "Task to Delete")
        repository.insertTask(task)

        val viewModel = DetailViewModel(repository, 1L)
        advanceUntilIdle()

        var callbackInvoked = false
        viewModel.deleteTask {
            callbackInvoked = true
        }
        advanceUntilIdle()

        assertTrue(callbackInvoked)
    }
}