package com.studyhub.presentation.progress

import com.studyhub.data.fake.FakePomodoroRepository
import com.studyhub.domain.fake.FakeTaskRepository
import com.studyhub.domain.fake.TaskBuilder
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.presentation.screens.progress.ProgressUiState
import com.studyhub.presentation.screens.progress.ProgressViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {
    private lateinit var fakeTaskRepo: FakeTaskRepository
    private lateinit var fakePomodoroRepo: FakePomodoroRepository
    private lateinit var viewModel: ProgressViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeTaskRepo = FakeTaskRepository()
        fakePomodoroRepo = FakePomodoroRepository()
        viewModel = ProgressViewModel(
            GetAllTasksUseCase(fakeTaskRepo),
            fakePomodoroRepo
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given no tasks when loadStats then state is Empty`() = runTest {
        viewModel.loadStats()
        assertTrue(viewModel.uiState.value is ProgressUiState.Empty)
    }

    @Test
    fun `given tasks when loadStats then state is Success`() = runTest {
        fakeTaskRepo.addTasks(listOf(
            TaskBuilder.build(id = "t1"),
            TaskBuilder.build(id = "t2", status = TaskStatus.DONE)
        ))
        viewModel.loadStats()
        assertTrue(viewModel.uiState.value is ProgressUiState.Success)
    }

    @Test
    fun `given completed tasks when loadStats then completionRate correct`() = runTest {
        fakeTaskRepo.addTasks(listOf(
            TaskBuilder.build(id = "t1", status = TaskStatus.DONE),
            TaskBuilder.build(id = "t2", status = TaskStatus.DONE),
            TaskBuilder.build(id = "t3", status = TaskStatus.TODO)
        ))
        viewModel.loadStats()
        val state = viewModel.uiState.value as? ProgressUiState.Success
        assertNotNull(state)
        assertEquals(0.66f, state.completionRate, 0.01f)
    }

    @Test
    fun `given tasks with subjects when loadStats then subjectProgress populated`() = runTest {
        fakeTaskRepo.addTasks(listOf(
            TaskBuilder.build(id = "t1", subject = "Algo"),
            TaskBuilder.build(id = "t2", subject = "Algo"),
            TaskBuilder.build(id = "t3", subject = "Mobile")
        ))
        viewModel.loadStats()
        val state = viewModel.uiState.value as? ProgressUiState.Success
        assertNotNull(state)
        assertEquals(2, state.subjectProgress.size)
    }

    @Test
    fun `given completed tasks today when loadStats then focusMinutes from pomodoro`() = runTest {
        fakePomodoroRepo.focusMinutesToday = 50
        fakeTaskRepo.addTasks(listOf(TaskBuilder.build(id = "t1")))
        viewModel.loadStats()
        val state = viewModel.uiState.value as? ProgressUiState.Success
        assertNotNull(state)
        assertEquals(50, state.focusMinutesToday)
    }

    @Test
    fun `given high priority tasks when loadStats then highPriorityRate calculated`() = runTest {
        fakeTaskRepo.addTasks(listOf(
            TaskBuilder.build(id = "t1", priority = Priority.HIGH, status = TaskStatus.DONE),
            TaskBuilder.build(id = "t2", priority = Priority.HIGH, status = TaskStatus.TODO)
        ))
        viewModel.loadStats()
        val state = viewModel.uiState.value as? ProgressUiState.Success
        assertNotNull(state)
        assertEquals(0.5f, state.highPriorityRate)
    }

    @Test
    fun `given consecutive completed days when loadStats then streak is correct`() = runTest {
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        fakeTaskRepo.addTasks(listOf(
            TaskBuilder.build(id = "t1", status = TaskStatus.DONE, completedAt = now - 86_400_000L),
            TaskBuilder.build(id = "t2", status = TaskStatus.DONE, completedAt = now)
        ))
        viewModel.loadStats()
        val state = viewModel.uiState.value as? ProgressUiState.Success
        assertNotNull(state)
        assertTrue(state.currentStreak >= 1)
    }

    @Test
    fun `given repo fails when loadStats then state is Error`() = runTest {
        fakeTaskRepo.shouldThrow = true
        viewModel.loadStats()
        assertTrue(viewModel.uiState.value is ProgressUiState.Error)
    }
}
