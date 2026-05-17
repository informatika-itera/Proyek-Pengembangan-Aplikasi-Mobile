package com.soundletter.app.presentation.screens.home

import app.cash.turbine.test
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

/**
 * Fake implementation dari LetterRepository untuk keperluan testing
 */
class FakeLetterRepository : LetterRepository {
    private val lettersFlow = MutableSharedFlow<List<Note>>()

    override fun getLetters(): Flow<List<Note>> = lettersFlow
    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun sendLetter(letter: Note) {}
    override suspend fun deleteLetter(id: Long) {}

    suspend fun emitLetters(letters: List<Note>) {
        lettersFlow.emit(letters)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeLetterRepository
    private lateinit var viewModel: HomeScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeLetterRepository()
        viewModel = HomeScreenViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state should update to loaded when repository emits letters`() = runTest {
        val mockLetters = listOf(
            Note(id = 1, recipient = "User", content = "Test Content")
        )

        viewModel.state.test {
            // State awal saat init (loading)
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)

            // Simulasikan emisi data dari repository
            repository.emitLetters(mockLetters)

            // Verifikasi state berubah menjadi terisi
            val loadedState = awaitItem()
            assertEquals(mockLetters, loadedState.letters)
            assertEquals(false, loadedState.isLoading)
        }
    }
}
