package com.example.Roomie.presentation.auth

import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.domain.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: LoginViewModel
    private val mockAuthRepo = mockk<com.example.Roomie.domain.repository.AuthRepository>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(LoginUseCase(mockAuthRepo))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login flow coverage - success and validation`() = runTest {
        val user = User("1", "Test", "121111111", UserRole.STUDENT)
        coEvery { mockAuthRepo.login(any()) } returns Result.success(user)
        
        viewModel.onIdNumberChange("121111111")
        viewModel.login()
        assertTrue(viewModel.state.value.loginSuccess)
        
        // Validation check
        viewModel.onIdNumberChange("99")
        viewModel.login()
        assertNotNull(viewModel.state.value.error)
    }

    @Test
    fun `login flow coverage - failure`() = runTest {
        coEvery { mockAuthRepo.login(any()) } returns Result.failure(Exception("Not Found"))
        
        viewModel.onIdNumberChange("121222222")
        viewModel.login()
        assertNotNull(viewModel.state.value.error)
    }
}
