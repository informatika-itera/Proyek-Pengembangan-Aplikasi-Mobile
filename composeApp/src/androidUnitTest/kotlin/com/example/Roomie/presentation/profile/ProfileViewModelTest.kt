package com.example.Roomie.presentation.profile

import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.remote.SupabaseService
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import com.example.Roomie.domain.usecase.GetAllReportsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ProfileViewModel
    
    private val mockGetCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val mockGetAllReportsUseCase = mockk<GetAllReportsUseCase>()
    private val mockUserPreferences = mockk<UserPreferences>(relaxed = true)
    private val mockSupabaseService = mockk<SupabaseService>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { mockGetCurrentUserUseCase() } returns flowOf(User("1", "Test", "123", UserRole.STUDENT))
        every { mockGetAllReportsUseCase() } returns flowOf(emptyList())
        every { mockUserPreferences.userAvatar } returns flowOf(null)
        
        viewModel = ProfileViewModel(
            mockGetCurrentUserUseCase,
            mockGetAllReportsUseCase,
            mockUserPreferences,
            mockSupabaseService
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load should set Success state`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state is ProfileUiState.Success)
        assertEquals("Test", (state as ProfileUiState.Success).user?.name)
    }

    @Test
    fun `updateAvatar success - should call upload and save`() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        coEvery { mockSupabaseService.uploadAvatar(any()) } returns "https://avatar.com"
        
        viewModel.updateAvatar(bytes)
        
        coVerify { mockSupabaseService.uploadAvatar(bytes) }
        coVerify { mockUserPreferences.saveAvatar("https://avatar.com") }
    }
}
