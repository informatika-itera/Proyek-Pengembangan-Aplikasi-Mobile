package com.example.edumate.presentation.screens.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.example.edumate.data.repository.FakeTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertTrue

@OptIn(
    ExperimentalTestApi::class,
    ExperimentalCoroutinesApi::class
)
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [33],
    application = android.app.Application::class
)
class HomeScreenUiTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestViewModel(): HomeViewModel {
        return HomeViewModel(FakeTaskRepository())
    }

    @Test
    fun homeScreen_clickFab_shouldTriggerNavigateToAdd() = runComposeUiTest {
        var isAddClicked = false

        setContent {
            HomeScreen(
                onOpenDrawer = {},
                onNavigateToAdd = {
                    isAddClicked = true
                },
                onNavigateToDetail = {},
                onNavigateToAIAssistant = {},
                viewModel = createTestViewModel()
            )
        }

        waitForIdle()

        onNodeWithContentDescription("Buat Tugas")
            .performClick()

        waitForIdle()

        assertTrue(isAddClicked)
    }
}