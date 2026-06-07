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

    override fun getGlobalLetters(): Flow<List<Note>> = emptyFlow()

    override fun searchLetters(query: String): Flow<List<Note>> = flow {
        if (shouldFail) throw Exception("Search Error")
        emitAll(flow.map { list -> 
            list.filter { it.recipient.contains(query, ignoreCase = true) } 
        })
    }
    
    override suspend fun getLetterById(id: Long): Note? = null
    
    override suspend fun sendLetter(letter: Note): Boolean = true
    
    override suspend fun deleteLetter(id: Long) {}
    
    override suspend fun clearHistory() {}

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
            
            assertIs<UiState.Loading>(awaitItem())
            
            repository.emit(mockData)
            
            val successState = awaitItem()
            assertIs<UiState.Success<List<Note>>>(successState)
            val results = successState.data
            assertEquals(1, results.size)
            assertEquals("Dzakky", results[0].recipient)
        }
    }

    @Test
    fun `onQueryChange should emit Success with empty list when no data matched`() = runTest {
        val mockData = listOf(Note(id = 1, recipient = "Dzakky", content = "Hi"))
        
        viewModel.searchState.test {
            assertIs<UiState.Idle>(awaitItem())
            viewModel.onQueryChange("Unknown")
            
            assertIs<UiState.Loading>(awaitItem())
            
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
            
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertIs<UiState.Error>(state)
            assertEquals("Search Error", (state as UiState.Error).message)
        }
    }
}
