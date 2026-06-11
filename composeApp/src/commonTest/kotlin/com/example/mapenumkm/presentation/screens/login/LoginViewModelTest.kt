package com.example.mapenumkm.presentation.screens.login

import com.example.mapenumkm.data.local.datastore.UserPreferences
import com.example.mapenumkm.domain.model.User
import com.example.mapenumkm.domain.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val userRepository: UserRepository = mockk()
    private val userPreferences: UserPreferences = mockk()
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(userRepository, userPreferences)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with blank identifier should return error`() {
        viewModel.login("", "password")
        assertTrue(viewModel.loginState.value is LoginState.Error)
    }

    @Test
    fun `login with correct credentials should return success`() = runTest {
        val user = User(id = 1, name = "Test", email = "test@test.com", phone = "123", password = "password", createdAt = 0L)
        coEvery { userRepository.getUserByEmailOrPhone("test@test.com") } returns user
        coEvery { userPreferences.setLoggedIn(true, "test@test.com") } just Runs

        viewModel.login("test@test.com", "password")

        assertEquals(LoginState.Success, viewModel.loginState.value)
    }

    @Test
    fun `login with wrong password should return error`() = runTest {
        val user = User(id = 1, name = "Test", email = "test@test.com", phone = "123", password = "password", createdAt = 0L)
        coEvery { userRepository.getUserByEmailOrPhone("test@test.com") } returns user

        viewModel.login("test@test.com", "wrong")

        assertTrue(viewModel.loginState.value is LoginState.Error)
    }
}
