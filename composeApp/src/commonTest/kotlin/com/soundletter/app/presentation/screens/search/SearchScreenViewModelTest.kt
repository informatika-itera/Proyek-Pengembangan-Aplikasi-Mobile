package com.soundletter.app.presentation.screens.search

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

class FakeSearchRepository : LetterRepository {
    private val flow = MutableSharedFlow<List<Note>>()
    var shouldFail = false

    override fun getLetters(): Flow<List<Note>> = flow {
        if (shouldFail) throw Exception("Search Error")
        emitAll(flow)
    }
    
    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun sendLetter(letter: Note) {}
    override suspend fun deleteLetter(id: Long) {}

    suspend fun emit(data: List<Note>) = flow.emit(data)
}

@OptIn(ExperimentalCoroutinesApi::class)
class SearchScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeSearchRepository
    private lateinit var viewModel: SearchScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeSearchRepository()
        viewModel = SearchScreenViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onQueryChange should filter and emit Success when data matched`() = runTest {
        val mockData = listOf(
            Note(id = 1, recipient = "Dzakky", content = "Hi"),
            Note(id = 2, recipient = "Atalie", content = "Hello")
        )
        
        viewModel.searchState.test {
            assertIs<UiState.Idle>(awaitItem())
            viewModel.onQueryChange("Dzakky")
            
            val loadingState = awaitItem()
            assertIs<UiState.Loading>(loadingState)
            
            repository.emit(mockData)
            
            val successState = awaitItem()
            assertIs<UiState.Success<List<Note>>>(successState)
            assertEquals(1, successState.data.size)
            assertEquals("Dzakky", successState.data[0].recipient)
        }
    }

    @Test
    fun `onQueryChange should emit Success with empty list when no data matched`() = runTest {
        val mockData = listOf(Note(id = 1, recipient = "Dzakky", content = "Hi"))
        
        viewModel.searchState.test {
            assertIs<UiState.Idle>(awaitItem())
            viewModel.onQueryChange("Unknown")
            
            val loadingState = awaitItem()
            assertIs<UiState.Loading>(loadingState)
            
            repository.emit(mockData)
            
            val successState = awaitItem()
            assertIs<UiState.Success<List<Note>>>(successState)
            assertEquals(0, successState.data.size)
        }
    }

    @Test
    fun `search failure should emit Error state`() = runTest {
        repository.shouldFail = true
        viewModel.searchState.test {
            assertIs<UiState.Idle>(awaitItem())
            viewModel.onQueryChange("Fail")
            
            val nextState = awaitItem()
            if (nextState is UiState.Loading) {
                val errorState = awaitItem()
                assertIs<UiState.Error>(errorState)
                assertEquals("Search Error", errorState.message)
            } else {
                assertIs<UiState.Error>(nextState)
                assertEquals("Search Error", (nextState as UiState.Error).message)
            }
        }
    }
}
