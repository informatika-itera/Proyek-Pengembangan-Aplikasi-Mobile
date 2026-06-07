package com.soundletter.app.presentation.screens.settings

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeSettingsLetterRepo : LetterRepository {
    var clearHistoryCalled = false
    override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
    override fun getGlobalLetters(): Flow<List<Note>> = flowOf(emptyList())
    override fun searchLetters(query: String): Flow<List<Note>> = flowOf(emptyList())
    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun sendLetter(letter: Note): Boolean = true
    override suspend fun deleteLetter(id: Long) {}
    override suspend fun clearHistory() { 
        yield() // Force suspension to avoid StateFlow conflation in tests
        clearHistoryCalled = true 
    }
}

class FakePreferenceRepo : PreferenceRepository {
    private val _isDarkMode = MutableStateFlow(false)
    override val isDarkMode = _isDarkMode
    override fun toggleDarkMode(enabled: Boolean) { _isDarkMode.value = enabled }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SettingsViewModel
    private lateinit var letterRepo: FakeSettingsLetterRepo
    private lateinit var prefRepo: FakePreferenceRepo

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        letterRepo = FakeSettingsLetterRepo()
        prefRepo = FakePreferenceRepo()
        viewModel = SettingsViewModel(letterRepo, prefRepo)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleDarkMode updates preference`() = runTest {
        viewModel.toggleDarkMode(true)
        assertEquals(true, prefRepo.isDarkMode.value)
    }

    @Test
    fun `clearLocalHistory triggers repository and emits Success`() = runTest {
        viewModel.clearHistoryStatus.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.clearLocalHistory()
            assertIs<UiState.Loading>(awaitItem())
            assertIs<UiState.Success<Unit>>(awaitItem())
            assertEquals(true, letterRepo.clearHistoryCalled)
        }
    }
}
