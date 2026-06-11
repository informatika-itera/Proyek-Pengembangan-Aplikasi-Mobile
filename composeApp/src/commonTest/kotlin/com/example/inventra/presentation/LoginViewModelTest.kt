package com.example.inventra.presentation

import androidx.compose.ui.text.input.TextFieldValue
import app.cash.turbine.test
import com.example.inventra.FakeAuthRepository
import com.example.inventra.presentation.screens.auth.LoginViewModel
import com.example.inventra.presentation.screens.auth.LoginUiState
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        viewModel = LoginViewModel(authRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty fields and no user`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.email.text)
            assertEquals("", state.password.text)
            assertNull(state.loggedInUser)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEmailChange should update email state`() = runTest {
        viewModel.onEmailChange(TextFieldValue("test@example.com"))
        viewModel.uiState.test {
            assertEquals("test@example.com", awaitItem().email.text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login should show error when fields are empty`() = runTest {
        viewModel.login()
        advanceUntilIdle()
        viewModel.uiState.test {
            assertEquals("Email dan password harus diisi", awaitItem().error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login should succeed with valid credentials`() = runTest {
        viewModel.onEmailChange(TextFieldValue(text = "admin@test.com"))
        viewModel.onPasswordChange(TextFieldValue(text = "password123"))
        
        viewModel.login()
        advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.loggedInUser)
            assertEquals("admin@test.com", state.loggedInUser?.email)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
