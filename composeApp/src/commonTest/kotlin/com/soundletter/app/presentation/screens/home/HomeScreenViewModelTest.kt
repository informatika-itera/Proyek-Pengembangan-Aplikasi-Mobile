package com.soundletter.app.presentation.screens.home

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeHomeRepository : LetterRepository {
    private val globalFlow = MutableSharedFlow<List<Note>>()
    private val localFlow = MutableSharedFlow<List<Note>>(replay = 1)
    var shouldFail = false

    init {
        localFlow.tryEmit(emptyList())
    }

    override fun getLetters(): Flow<List<Note>> = localFlow

    override fun getGlobalLetters(): Flow<List<Note>> = flow {
        if (shouldFail) throw Exception("Network Error")
        emitAll(globalFlow)
    }

    override fun searchLetters(query: String): Flow<List<Note>> = emptyFlow()
    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun sendLetter(letter: Note): Boolean = true
    override suspend fun deleteLetter(id: Long) {}
    override suspend fun clearHistory() {}

    suspend fun emitGlobal(data: List<Note>) = globalFlow.emit(data)
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeHomeRepository
    private lateinit var viewModel: HomeScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHomeRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLetters success should emit Success state`() = runTest {
        viewModel = HomeScreenViewModel(repository)
        val mockData = listOf(Note(id = 1, recipient = "Test", content = "Msg"))
        
        viewModel.uiState.test {
            var state = awaitItem()
            
            // Tunggu sampai Loading atau Success (jika emisi sangat cepat)
            if (state is UiState.Idle || state is UiState.Loading) {
                repository.emitGlobal(mockData)
                state = awaitItem()
                // Jika masih loading, tunggu emisi berikutnya
                if (state is UiState.Loading) {
                    state = awaitItem()
                }
            }
            
            assertIs<UiState.Success<List<Note>>>(state)
            assertEquals(1, state.data.size)
        }
    }

    @Test
    fun `loadLetters failure should emit Error state`() = runTest {
        repository.shouldFail = true
        viewModel = HomeScreenViewModel(repository)
        
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is UiState.Idle || state is UiState.Loading) {
                state = awaitItem()
            }
            assertIs<UiState.Error>(state)
            assertEquals("Network Error", state.message)
        }
    }
}
