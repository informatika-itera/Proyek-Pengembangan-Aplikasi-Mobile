package com.example.masakuy.domain.usecase

import com.example.masakuy.presentation.screens.auth.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login set isLoading true saat proses berjalan`() = runTest {
        viewModel.login("user@email.com", "password123")

        // Sebelum delay selesai
        advanceTimeBy(100)

        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `login set isSuccess true setelah selesai`() = runTest {
        viewModel.login("user@email.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.error)
    }

    @Test
    fun `login set isLoading false setelah selesai`() = runTest {
        viewModel.login("user@email.com", "password123")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login dengan email kosong tetap berjalan`() = runTest {
        viewModel.login("", "")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `state awal isLoading false dan isSuccess false`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.error)
    }
}
