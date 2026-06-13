package com.example.Roomie.presentation.home

import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    
    private val mockGetCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val mockGetAllReportsUseCase = mockk<GetAllReportsUseCase>()
    private val mockGetAllAnnouncementsUseCase = mockk<GetAllAnnouncementsUseCase>()
    private val mockPerformAutomaticCleanupUseCase = mockk<PerformAutomaticCleanupUseCase>(relaxed = true)
    private val mockUserPreferences = mockk<UserPreferences>(relaxed = true)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { mockGetCurrentUserUseCase() } returns flowOf(User("1", "Test", "123", UserRole.STUDENT))
        every { mockGetAllReportsUseCase() } returns flowOf(emptyList())
        every { mockGetAllAnnouncementsUseCase() } returns flowOf(emptyList())
        every { mockUserPreferences.userAvatar } returns flowOf(null)
        
        viewModel = HomeViewModel(
            mockGetCurrentUserUseCase,
            mockGetAllReportsUseCase,
            mockGetAllAnnouncementsUseCase,
            mockPerformAutomaticCleanupUseCase,
            mockUserPreferences
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load should call cleanup and set Success state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockPerformAutomaticCleanupUseCase() }
        
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals("Test", (state as HomeUiState.Success).userName)
    }
}
