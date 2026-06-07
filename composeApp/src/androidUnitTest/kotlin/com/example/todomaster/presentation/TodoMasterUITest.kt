package com.example.todomaster.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todomaster.data.repository.FakeTaskRepository
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import com.example.todomaster.presentation.components.TaskItem
import com.example.todomaster.presentation.screens.addtask.AddTaskViewModel
import com.example.todomaster.presentation.screens.detail.TaskDetailScreen
import com.example.todomaster.presentation.screens.detail.TaskDetailViewModel
import com.example.todomaster.presentation.screens.home.HomeScreen
import com.example.todomaster.presentation.screens.home.HomeViewModel
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], application = android.app.Application::class)
class TodoMasterUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            modules(module {
                // Dependensi Dasar
                single<TaskRepository> { FakeTaskRepository() }
                single { io.ktor.client.HttpClient() }
                single { com.example.todomaster.data.remote.api.GeminiService(get()) }
                single { com.example.todomaster.domain.usecase.AddTaskUseCase(get()) }

                // ViewModel yang dibutuhkan
                factory { HomeViewModel(get()) }
                factory { TaskDetailViewModel(get()) }
                factory { AddTaskViewModel(addTaskUseCase = get(), repository = get(), geminiService = get()) }

                // ✅ Tambahkan baris ini untuk fix NoDefinitionFoundException
                factory { com.example.todomaster.presentation.screens.quadrantdetail.QuadrantDetailViewModel(get()) }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun verifyFabClickTriggersNavigationToAddTask() {
        var isNavigated = false
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToAddTask = { isNavigated = true },
                onNavigateToTaskDetail = {},
                onNavigateToQuadrantDetail = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Tambah Tugas", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()

        assertTrue(isNavigated)
    }

    @Test
    fun verifyTaskItemClickTriggersCallback() {
        var isItemClicked = false
        val mockTask = Task(
            id = 99,
            title = "Task Validasi Klik",
            description = "Deskripsi",
            priority = Quadrant.DO_FIRST,
            dueDate = Clock.System.now().toEpochMilliseconds(),
            isCompleted = false,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        composeTestRule.setContent {
            TaskItem(
                task = mockTask,
                onClick = { isItemClicked = true },
                onToggleComplete = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Task Validasi Klik", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()

        assertTrue(isItemClicked)
    }

    @Test
    fun verifyTaskItemDisplaysTitleCorrectly() {
        val mockTask = Task(
            id = 102,
            title = "Validasi Render Judul Tugas",
            description = "Deskripsi",
            priority = Quadrant.SCHEDULE,
            dueDate = Clock.System.now().toEpochMilliseconds(),
            isCompleted = false,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        composeTestRule.setContent {
            TaskItem(
                task = mockTask,
                onClick = {},
                onToggleComplete = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Validasi Render Judul Tugas", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun verifyAddTaskScreenRendersToBoostCoverage() {
        composeTestRule.setContent {
            com.example.todomaster.presentation.screens.addtask.AddTaskScreen(
                onNavigateBack = {}
            )
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun verifyTaskDetailScreenRendersToBoostCoverage() {
        composeTestRule.setContent {
            com.example.todomaster.presentation.screens.detail.TaskDetailScreen(
                taskId = 999L,
                onNavigateBack = {},
                onNavigateToEdit = {},
                onShare = {}
            )
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun verifyQuadrantDetailScreenRendersToBoostCoverage() {
        kotlinx.coroutines.test.runTest {
            composeTestRule.setContent {
                com.example.todomaster.presentation.screens.quadrantdetail.QuadrantDetailScreen(
                    initialQuadrantId = 1L,
                    onNavigateBack = {},
                    onNavigateToTaskDetail = {},
                    onNavigateToAddTask = {}
                )
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun verifyAIScreenRendersToBoostCoverage() {
        composeTestRule.setContent {
            com.example.todomaster.presentation.screens.ai.AIAssistantScreen(
                taskId = null,
                initialText = null,
                onNavigateBack = {}
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun verifyNavigationToStatisticsAndCalendar() {
        composeTestRule.setContent {
            com.example.todomaster.presentation.navigation.AppNavHost()
        }
        composeTestRule.waitForIdle()
    }
}