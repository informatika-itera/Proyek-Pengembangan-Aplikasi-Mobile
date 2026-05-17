package com.soundletter.app.presentation.screens.detail

import app.cash.turbine.test
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

class FakeDetailRepository : LetterRepository {
    override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
    override suspend fun getLetterById(id: Long): Note? {
        return if (id == 1L) {
            Note(id = 1L, recipient = "Test", content = "Content")
        } else null
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
    fun `loadMessage should update state with correct note`() = runTest {
        viewModel.state.test {
            // Initial state
            assertEquals(null, awaitItem().message)

            // Load existing ID
            viewModel.loadMessage("1")
            
            // Skip loading state
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)

            // Final state with message
            val finalState = awaitItem()
            assertEquals("1", finalState.message?.id?.toString())
            assertEquals(false, finalState.isLoading)
        }
    }
}
