package com.example.raillog.presentation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.presentation.screens.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.*

/**
 * Fake in-memory DataStore<Preferences> untuk testing UserPreferences/LoginViewModel
 * tanpa perlu filesystem access.
 */
class FakeDataStore : DataStore<Preferences> {
    private val state = MutableStateFlow(emptyPreferences())
    override val data: Flow<Preferences> = state.asStateFlow()

    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() { Dispatchers.setMain(testDispatcher) }

    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `login with admin credentials sets role to admin`() = runTest {
        val prefs = UserPreferences(FakeDataStore())
        val viewModel = LoginViewModel(prefs)

        viewModel.login("admin", "raillog123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.loginSuccess)
        assertEquals("admin", state.role)
        assertEquals("admin", prefs.userRole.first())
    }

    @Test
    fun `login with default operator credentials sets role to staff`() = runTest {
        val prefs = UserPreferences(FakeDataStore())
        val viewModel = LoginViewModel(prefs)

        viewModel.login("operator", "raillog123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.loginSuccess)
        assertEquals("staff", state.role)
    }

    @Test
    fun `login with wrong credentials shows error and does not succeed`() = runTest {
        val prefs = UserPreferences(FakeDataStore())
        val viewModel = LoginViewModel(prefs)

        viewModel.login("admin", "wrongpassword")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.loginSuccess)
        assertEquals("Username atau Password salah!", state.error)
    }

    @Test
    fun `login with blank fields shows validation error`() = runTest {
        val prefs = UserPreferences(FakeDataStore())
        val viewModel = LoginViewModel(prefs)

        viewModel.login("", "")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.loginSuccess)
        assertEquals("Username dan Password wajib diisi!", state.error)
    }

    @Test
    fun `login with registered staff credentials takes priority over default operator`() = runTest {
        val prefs = UserPreferences(FakeDataStore())
        prefs.registerStaff(
            name = "Giovan Lado",
            user = "giovan",
            pass = "secret123",
            employeeId = "RLN-001",
            phone = "081234567890"
        )

        val viewModel = LoginViewModel(prefs)
        viewModel.login("giovan", "secret123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.loginSuccess)
        assertEquals("staff", state.role)
    }

    @Test
    fun `resetState clears uiState back to default`() = runTest {
        val prefs = UserPreferences(FakeDataStore())
        val viewModel = LoginViewModel(prefs)

        viewModel.login("admin", "raillog123")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.loginSuccess)

        viewModel.resetState()

        val state = viewModel.uiState.value
        assertFalse(state.loginSuccess)
        assertNull(state.error)
        assertEquals("", state.role)
    }
}

// Tetap pertahankan test sederhana untuk PartCategory (sudah valid sebelumnya)
class SupplyItemMappingTest {
    @Test
    fun `test PartCategory fromString fallback`() {
        val category = com.example.raillog.domain.model.PartCategory.fromString("INVALID")
        assertEquals(com.example.raillog.domain.model.PartCategory.MAINTENANCE, category)
    }

    @Test
    fun `test PartCategory fromString valid`() {
        val category = com.example.raillog.domain.model.PartCategory.fromString("BOGIE")
        assertEquals(com.example.raillog.domain.model.PartCategory.BOGIE, category)
    }
}
