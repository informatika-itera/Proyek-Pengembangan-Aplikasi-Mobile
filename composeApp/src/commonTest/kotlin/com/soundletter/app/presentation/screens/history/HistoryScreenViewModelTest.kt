package com.soundletter.app.presentation.screens.history

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeHistoryRepository : LetterRepository {
    private val flow = MutableSharedFlow<List<Note>>()
    var shouldFail = false
    var lastDeletedId: Long? = null

    override fun getLetters(): Flow<List<Note>> {
        if (shouldFail) throw Exception("Database Connection Error")
        return flow
    }
    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun sendLetter(letter: Note) {}
    override suspend fun deleteLetter(id: Long) {
        lastDeletedId = id
    }

    suspend fun emit(data: List<Note>) = flow.emit(data)
}

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeHistoryRepository
    private lateinit var viewModel: HistoryScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHistoryRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadHistory success should emit Success state`() = runTest {
        viewModel = HistoryScreenViewModel(repository)
        val mockData = listOf(Note(id = 1, recipient = "Test", content = "Msg"))
        viewModel.historyState.test {
            val initialState = awaitItem()
            if (initialState is UiState.Loading) {
                repository.emit(mockData)
                val successState = awaitItem()
                assertIs<UiState.Success<List<Note>>>(successState)
            } else {
                // Jika langsung success (conflated)
                assertIs<UiState.Success<List<Note>>>(initialState)
            }
        }
    }

    @Test
    fun `loadHistory failure should emit Error state`() = runTest {
        repository.shouldFail = true
        viewModel = HistoryScreenViewModel(repository)
        
        viewModel.historyState.test {
            val state = awaitItem()
            // Karena error dilempar di init, state mungkin sudah Error saat mulai test
            if (state is UiState.Loading) {
                assertIs<UiState.Error>(awaitItem())
            } else {
                assertIs<UiState.Error>(state)
                assertEquals("Database Connection Error", (state as UiState.Error).message)
            }
        }
    }

    @Test
    fun `deleteLetter should call repository delete`() = runTest {
        viewModel = HistoryScreenViewModel(repository)
        val testId = 123L
        viewModel.deleteLetter(testId)
        assertEquals(testId, repository.lastDeletedId)
    }
}
