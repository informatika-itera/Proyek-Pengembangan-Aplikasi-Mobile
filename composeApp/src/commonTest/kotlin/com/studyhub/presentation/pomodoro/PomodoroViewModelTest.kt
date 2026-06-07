package com.studyhub.presentation.pomodoro

import com.studyhub.core.fake.FakeNotificationManager
import com.studyhub.core.manager.PomodoroManager
import com.studyhub.data.fake.FakePomodoroRepository
import com.studyhub.domain.fake.FakeGetUserPreferencesUseCase
import com.studyhub.domain.fake.FakeNotifHistoryRepository
import com.studyhub.domain.fake.FakeTaskRepository
import com.studyhub.domain.fake.TaskBuilder
import com.studyhub.domain.model.PomodoroPhase
import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.usecase.task.GetTaskByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroViewModelTest {
    private lateinit var fakePomodoroRepo: FakePomodoroRepository
    private lateinit var fakeNotifHistoryRepo: FakeNotifHistoryRepository
    private lateinit var fakeNotifManager: FakeNotificationManager
    private lateinit var fakePrefsUseCase: FakeGetUserPreferencesUseCase
    private lateinit var fakeTaskRepo: FakeTaskRepository
    private lateinit var pomodoroManager: PomodoroManager
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePomodoroRepo = FakePomodoroRepository()
        fakeNotifHistoryRepo = FakeNotifHistoryRepository()
        fakeNotifManager = FakeNotificationManager()
        fakePrefsUseCase = FakeGetUserPreferencesUseCase()
        fakeTaskRepo = FakeTaskRepository()
        
        pomodoroManager = PomodoroManager(
            fakePomodoroRepo,
            fakeNotifHistoryRepo,
            fakeNotifManager,
            fakePrefsUseCase,
            GetTaskByIdUseCase(fakeTaskRepo),
            TestScope(testDispatcher)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given initial state when created then phase is FOCUS`() = runTest {
        assertEquals(PomodoroPhase.FOCUS, pomodoroManager.state.value.phase)
    }

    @Test
    fun `given initial state when created then isRunning is false`() = runTest {
        assertFalse(pomodoroManager.state.value.isRunning)
    }

    @Test
    fun `given paused when start then isRunning becomes true`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        pomodoroManager.startTimer()
        assertTrue(pomodoroManager.state.value.isRunning)
    }

    @Test
    fun `given running when pause then isRunning becomes false`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        pomodoroManager.startTimer()
        pomodoroManager.pauseTimer()
        assertFalse(pomodoroManager.state.value.isRunning)
    }

    @Test
    fun `given running when reset then time restored to full`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val totalSecs = pomodoroManager.state.value.totalSeconds
        pomodoroManager.startTimer()
        pomodoroManager.resetTimer()
        assertEquals(totalSecs, pomodoroManager.state.value.timeRemainingSeconds)
    }

    @Test
    fun `given valid taskId when linkTask then task is linked`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        fakeTaskRepo.addTasks(listOf(TaskBuilder.build(id = "t1", title = "Algo Task")))
        pomodoroManager.linkTask("t1")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Algo Task", pomodoroManager.state.value.linkedTaskTitle)
    }

    @Test
    fun `given null taskId when linkTask then task is unlinked`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        fakeTaskRepo.addTasks(listOf(TaskBuilder.build(id = "t1", title = "Algo Task")))
        pomodoroManager.linkTask("t1")
        testDispatcher.scheduler.advanceUntilIdle()
        pomodoroManager.linkTask(null)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(pomodoroManager.state.value.linkedTaskTitle)
    }

    @Test
    fun `given settings when loaded then focusDuration matches prefs`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        fakePrefsUseCase.prefs = UserPreferences(pomodoroFocusDuration = 30)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(30 * 60, pomodoroManager.state.value.totalSeconds)
    }
}
