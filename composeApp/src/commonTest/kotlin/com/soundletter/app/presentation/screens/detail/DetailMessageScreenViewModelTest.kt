package com.soundletter.app.presentation.screens.detail

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeDetailRepository : LetterRepository {
    var shouldFail = false
    override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
    override suspend fun getLetterById(id: Long): Note? {
        if (shouldFail) throw Exception("Network Error")
        return if (id == 1L) Note(id = 1L, recipient = "Test", content = "Content") else null
    }
    override suspend fun sendLetter(letter: Note) {}
    override suspend fun deleteLetter(id: Long) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class DetailMessageScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeDetailRepository
    private lateinit var viewModel: DetailMessageScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeDetailRepository()
        viewModel = DetailMessageScreenViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMessage with valid ID should emit Success`() = runTest {
        viewModel.state.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.loadMessage("1")
            
            // Mengingat UnconfinedTestDispatcher sangat cepat, 
            // kita mungkin melewati Loading dan langsung ke Success
            val finalState = awaitItem()
            if (finalState is UiState.Loading) {
                assertIs<UiState.Success<Note>>(awaitItem())
            } else {
                assertIs<UiState.Success<Note>>(finalState)
            }
        }
    }

    @Test
    fun `loadMessage with invalid ID should emit Error`() = runTest {
        viewModel.state.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.loadMessage("99")
            
            val finalState = awaitItem()
            if (finalState is UiState.Loading) {
                val error = awaitItem()
                assertIs<UiState.Error>(error)
                assertEquals("Letter not found", error.message)
            } else {
                assertIs<UiState.Error>(finalState)
                assertEquals("Letter not found", (finalState as UiState.Error).message)
            }
        }
    }

    @Test
    fun `loadMessage with exception should emit Error with message`() = runTest {
        repository.shouldFail = true
        viewModel.state.test {
            assertEquals(UiState.Idle, awaitItem())
            viewModel.loadMessage("1")
            
            val finalState = awaitItem()
            if (finalState is UiState.Loading) {
                val error = awaitItem()
                assertIs<UiState.Error>(error)
            } else {
                assertIs<UiState.Error>(finalState)
            }
        }
    }
}
