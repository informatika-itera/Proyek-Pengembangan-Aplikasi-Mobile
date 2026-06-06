package com.example.edumate.presentation.screens.add

import com.example.edumate.data.repository.FakeTaskRepository
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.model.TaskPriority
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelTest {

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
    fun initialState_withoutTaskId_shouldSetDefaultDeadline() = runTest {
        val viewModel = AddEditViewModel(repository, null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.deadlineText.isNotEmpty())
        assertEquals(TaskPriority.MEDIUM, state.priority)
        assertEquals("", state.title)
    }

    @Test
    fun initialState_withTaskId_shouldLoadTaskDetails() = runTest {
        val task = Task(id = 1, title = "Task Lama", description = "Desc")
        repository.insertTask(task)

        val viewModel = AddEditViewModel(repository, 1L)
        advanceUntilIdle() // Tunggu coroutine loadTask selesai

        val state = viewModel.uiState.value
        assertEquals("Task Lama", state.title)
        assertEquals("Desc", state.description)
        assertFalse(state.isLoading)
    }

    @Test
    fun saveTask_withEmptyTitle_shouldShowError() = runTest {
        val viewModel = AddEditViewModel(repository, null)
        advanceUntilIdle()

        viewModel.onEvent(AddEditEvent.EnteredTitle("   "))
        viewModel.onEvent(AddEditEvent.SaveTask)
        advanceUntilIdle()

        assertEquals("Judul tidak boleh kosong", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun saveTask_withInvalidDeadline_shouldShowError() = runTest {
        val viewModel = AddEditViewModel(repository, null)
        advanceUntilIdle()

        viewModel.onEvent(AddEditEvent.EnteredTitle("Valid Title"))
        viewModel.onEvent(AddEditEvent.EnteredDeadline("Bukan Tanggal"))
        viewModel.onEvent(AddEditEvent.SaveTask)
        advanceUntilIdle()

        assertEquals("Format deadline harus YYYY-MM-DD", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun saveTask_withValidData_shouldSaveSuccessfully() = runTest {
        val viewModel = AddEditViewModel(repository, null)
        advanceUntilIdle()

        viewModel.onEvent(AddEditEvent.EnteredTitle("Tugas Baru"))
        viewModel.onEvent(AddEditEvent.EnteredDescription("Deskripsi Baru"))
        viewModel.onEvent(AddEditEvent.EnteredDeadline("2026-12-31"))
        viewModel.onEvent(AddEditEvent.SaveTask)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaved)
    }
}