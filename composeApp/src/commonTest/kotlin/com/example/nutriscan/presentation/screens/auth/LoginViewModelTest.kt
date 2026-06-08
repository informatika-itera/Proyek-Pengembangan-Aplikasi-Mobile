package com.example.nutriscan.presentation.screens.auth

import com.example.nutriscan.data.repository.FakeSessionRepository
import com.example.nutriscan.domain.model.UserRole
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
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        sessionRepository = FakeSessionRepository()
        viewModel = LoginViewModel(sessionRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `form awal menggunakan role USER`() {
        assertEquals("", viewModel.form.value.name)
        assertEquals(UserRole.USER, viewModel.form.value.role)
        assertEquals(false, viewModel.form.value.submitting)
    }

    @Test
    fun `onNameChange mengubah nama`() {
        viewModel.onNameChange("Budi")

        assertEquals("Budi", viewModel.form.value.name)
    }

    @Test
    fun `selectRole mengubah role`() {
        viewModel.selectRole(UserRole.NUTRITIONIST)

        assertEquals(UserRole.NUTRITIONIST, viewModel.form.value.role)
    }

    @Test
    fun `login dengan nama mengubah session`() = runTest {
        viewModel.onNameChange("Budi")
        viewModel.login()

        advanceUntilIdle()

        val state = sessionRepository.currentState()
        assertTrue(state.isLoggedIn)
        assertEquals(UserRole.USER, state.role)
        assertEquals("Budi", state.userName)
    }

    @Test
    fun `login user tanpa nama memakai fallback Pengguna`() = runTest {
        viewModel.login()

        advanceUntilIdle()

        assertEquals("Pengguna", sessionRepository.currentState().userName)
    }

    @Test
    fun `login nutritionist tanpa nama memakai fallback Ahli Gizi`() = runTest {
        viewModel.selectRole(UserRole.NUTRITIONIST)
        viewModel.login()

        advanceUntilIdle()

        val state = sessionRepository.currentState()
        assertEquals(UserRole.NUTRITIONIST, state.role)
        assertEquals("Ahli Gizi", state.userName)
    }
}