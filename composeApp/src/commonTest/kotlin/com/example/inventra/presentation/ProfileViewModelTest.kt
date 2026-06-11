package com.example.inventra.presentation

import app.cash.turbine.test
import com.example.inventra.FakeAuthRepository
import com.example.inventra.FakeBorrowRepository
import com.example.inventra.FakeItemRepository
import com.example.inventra.FakeUserPreferences
import com.example.inventra.domain.model.User
import com.example.inventra.domain.model.UserDivision
import com.example.inventra.domain.model.UserRole
import com.example.inventra.presentation.screens.profile.ProfileViewModel
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
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var itemRepository: FakeItemRepository
    private lateinit var borrowRepository: FakeBorrowRepository
    private lateinit var userPreferences: FakeUserPreferences
    private lateinit var viewModel: ProfileViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        itemRepository = FakeItemRepository()
        borrowRepository = FakeBorrowRepository()
        userPreferences = FakeUserPreferences()

        // Mock logged in user
        authRepository.loggedInUser = User(
            id = "user-id",
            name = "Test User",
            email = "test@example.com",
            role = UserRole.MEMBER,
            division = UserDivision.PUBDOK,
            phone = "0812345678"
        )

        viewModel = ProfileViewModel(authRepository, itemRepository, borrowRepository, userPreferences)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfile should update state with user data`() = runTest {
        viewModel.loadProfile()
        advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Test User", state.user?.name)
            assertEquals("0812345678", state.user?.phone)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `enterEditMode should set isEditMode to true`() = runTest {
        viewModel.enterEditMode()
        viewModel.uiState.test {
            assertTrue(awaitItem().isEditMode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `exitEditMode should set isEditMode to false`() = runTest {
        viewModel.exitEditMode()
        viewModel.uiState.test {
            assertFalse(awaitItem().isEditMode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNameChange should update editName`() = runTest {
        viewModel.onNameChange("New Name")
        viewModel.uiState.test {
            assertEquals("New Name", awaitItem().editName)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
