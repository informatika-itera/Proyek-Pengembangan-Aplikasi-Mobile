package com.example.inventra.presentation

import app.cash.turbine.test
import com.example.inventra.FakeAuthRepository
import com.example.inventra.domain.model.UserDivision
import com.example.inventra.domain.model.UserRole
import com.example.inventra.presentation.screens.management.UserManagementViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserManagementViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var viewModel: UserManagementViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        viewModel = UserManagementViewModel(authRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load users`() = runTest {
        advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.users.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `registerUser should succeed with valid data`() = runTest {
        val name = "New Admin"
        viewModel.onNewNameChange(name)
        viewModel.onNewEmailChange("admin2@test.com")
        viewModel.onNewPasswordChange("pass123456") // Length must be >= 6
        
        viewModel.registerUser()
        advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Akun berhasil dibuat untuk $name", state.successMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `registerUser should show error when email is empty`() = runTest {
        viewModel.onNewNameChange("No Email")
        viewModel.onNewEmailChange("")
        viewModel.onNewPasswordChange("pass123")
        
        viewModel.registerUser()
        advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Semua field wajib diisi", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
