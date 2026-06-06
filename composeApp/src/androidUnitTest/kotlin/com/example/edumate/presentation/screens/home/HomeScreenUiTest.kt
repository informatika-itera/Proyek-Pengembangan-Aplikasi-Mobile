package com.example.edumate.presentation.screens.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
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

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = android.app.Application::class)
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
        val fakeRepository = FakeTaskRepository()
        return HomeViewModel(fakeRepository)
    }

    @Test
    fun homeScreen_shouldDisplayEmptyState_whenNoTasks() = runComposeUiTest {
        val viewModel = createTestViewModel()

        setContent {
            HomeScreen(
                onNavigateToAdd = {},
                onNavigateToDetail = {},
                onNavigateToAIAssistant = {},
                onNavigateToTimer = {},
                onNavigateToStatistics = {},
                onNavigateToSettings = {},
                onNavigateToProfile = {},
                viewModel = viewModel
            )
        }

        onNodeWithText("Tidak ada tugas yang terdaftar saat ini.").assertExists()
    }

    @Test
    fun homeScreen_clickFab_shouldTriggerNavigateToAdd() = runComposeUiTest {
        var isAddClicked = false
        val viewModel = createTestViewModel()

        setContent {
            HomeScreen(
                onNavigateToAdd = { isAddClicked = true },
                onNavigateToDetail = {},
                onNavigateToAIAssistant = {},
                onNavigateToTimer = {},
                onNavigateToStatistics = {},
                onNavigateToSettings = {},
                onNavigateToProfile = {},
                viewModel = viewModel
            )
        }

        onNodeWithContentDescription("Buat Tugas").performClick()

        // Pastikan UI diam (idle) sebelum melakukan asersi
        waitForIdle()

        assertTrue(isAddClicked)
    }

    @Test
    fun homeScreen_drawerNavigation_shouldTriggerCallbacks() = runComposeUiTest {
        var isTimerClicked = false
        val viewModel = createTestViewModel()

        setContent {
            HomeScreen(
                onNavigateToAdd = {},
                onNavigateToDetail = {},
                onNavigateToAIAssistant = {},
                onNavigateToTimer = { isTimerClicked = true },
                onNavigateToStatistics = {},
                onNavigateToSettings = {},
                onNavigateToProfile = {},
                viewModel = viewModel
            )
        }

        // 1. Klik tombol buka laci navigasi
        onNodeWithContentDescription("Buka navigasi").performClick()

        // 2. TUNGGU animasi laci selesai terbuka dengan sempurna
        waitForIdle()

        // 3. Klik menu "Fokus Belajar"
        onNodeWithText("Fokus Belajar").performClick()

        // 4. TUNGGU proses klik dan coroutine scope.launch { drawerState.close() } diselesaikan
        waitForIdle()

        // 5. Lakukan verifikasi
        assertTrue(isTimerClicked)
    }
}