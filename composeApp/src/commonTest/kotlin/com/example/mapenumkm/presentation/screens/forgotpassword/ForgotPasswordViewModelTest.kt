package com.example.mapenumkm.presentation.screens.forgotpassword

import com.example.mapenumkm.domain.model.User
import com.example.mapenumkm.domain.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: ForgotPasswordViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel(userRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `resetPassword success when user found`() = runTest {
        val user = User(id = 1, name = "Test", email = "test@test.com", phone = "123", password = "old", createdAt = 0L)
        coEvery { userRepository.getUserByEmailOrPhone("test@test.com") } returns user
        coEvery { userRepository.updatePassword("test@test.com", "new") } returns Unit

        viewModel.resetPassword("test@test.com", "new")
        advanceUntilIdle()

        assertEquals(ForgotPasswordState.Success, viewModel.state.value)
        coVerify { userRepository.updatePassword("test@test.com", "new") }
    }

    @Test
    fun `resetPassword error when user not found`() = runTest {
        coEvery { userRepository.getUserByEmailOrPhone("nonexistent") } returns null

        viewModel.resetPassword("nonexistent", "new")
        advanceUntilIdle()

        assertTrue(viewModel.state.value is ForgotPasswordState.Error)
        assertEquals("Email atau nomor HP tidak terdaftar", (viewModel.state.value as ForgotPasswordState.Error).message)
    }

    @Test
    fun `resetPassword error when exception occurs`() = runTest {
        coEvery { userRepository.getUserByEmailOrPhone(any<String>()) } throws Exception("Database error")

        viewModel.resetPassword("test@test.com", "new")
        advanceUntilIdle()

        assertTrue(viewModel.state.value is ForgotPasswordState.Error)
        val errorMessage = (viewModel.state.value as ForgotPasswordState.Error).message
        assertTrue(errorMessage.contains("Database error"))
    }

    @Test
    fun `resetState sets state to Idle`() = runTest {
        coEvery { userRepository.getUserByEmailOrPhone(any<String>()) } returns null
        viewModel.resetPassword("test@test.com", "new")
        advanceUntilIdle()

        viewModel.resetState()
        assertEquals(ForgotPasswordState.Idle, viewModel.state.value)
    }
}
