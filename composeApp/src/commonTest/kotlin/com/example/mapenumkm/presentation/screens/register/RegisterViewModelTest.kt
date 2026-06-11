package com.example.mapenumkm.presentation.screens.register

import com.example.mapenumkm.data.local.datastore.UserPreferences
import com.example.mapenumkm.domain.model.User
import com.example.mapenumkm.domain.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val userRepository: UserRepository = mockk()
    private val userPreferences: UserPreferences = mockk()
    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(userRepository, userPreferences)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register success should transition to Success state`() = runTest {
        coEvery { userRepository.getUserByEmailOrPhone(any()) } returns null
        coEvery { userRepository.insertUser(any()) } returns Unit
        coEvery { userPreferences.setLoggedIn(true, any()) } just Runs

        viewModel.register("Name", "email@test.com", "123", "pass")

        assertEquals(RegisterState.Success, viewModel.registerState.value)
    }

    @Test
    fun `register with existing email should transition to Error state`() = runTest {
        val user = User(id = 1, name = "Test", email = "email@test.com", phone = "123", password = "pass", createdAt = 0L)
        coEvery { userRepository.getUserByEmailOrPhone("email@test.com") } returns user

        viewModel.register("Name", "email@test.com", "123", "pass")

        assertTrue(viewModel.registerState.value is RegisterState.Error)
    }
}
