package com.example.bookku.presentation

import app.cash.turbine.test
import com.example.bookku.data.repository.FakeNoteRepository
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.BookRating
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.usecase.deleteBookUseCase
import com.example.bookku.domain.usecase.GetAllNotesUseCase
import com.example.bookku.domain.usecase.NoteSortBy
import com.example.bookku.domain.usecase.SearchNotesUseCase
import com.example.bookku.presentation.screens.home.HomeUiState
import com.example.bookku.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeNoteRepository
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase
    private lateinit var deleteBookUseCase: deleteBookUseCase
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeNoteRepository()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
        deleteBookUseCase = deleteBookUseCase(repository)
        
        viewModel = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteBookUseCase = deleteBookUseCase,
            repository = repository
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            val loading = awaitItem()
            assertTrue(loading is HomeUiState.Loading)
            
            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is HomeUiState.Empty)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `state should be Success when books exist`() = runTest {
        repository.addBook(createTestNote("book 1"))
        repository.addBook(createTestNote("book 2"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteBookUseCase = deleteBookUseCase,
            repository = repository
        )
        
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).books.size)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `search should filter books by query`() = runTest {
        repository.addBook(createTestNote("Kotlin Guide"))
        repository.addBook(createTestNote("Java Tutorial"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteBookUseCase = deleteBookUseCase,
            repository = repository
        )
        
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            skipItems(1) // Skip initial success
            
            vm.onSearchQueryChange("Kotlin")
            
            testScheduler.advanceTimeBy(400) // Debounce
            advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).books.size)
            assertEquals("Kotlin Guide", state.books.first().title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `clearSearch should reset query`() = runTest {
        viewModel.onSearchQueryChange("test query")
        viewModel.clearSearch()
        
        viewModel.uiState.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()
            when (state) {
                is HomeUiState.Success -> assertEquals("", state.query)
                is HomeUiState.Empty -> assertEquals("", state.query)
                else -> {}
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `category filter should filter books`() = runTest {
        repository.addBook(createTestNote("Work book", category = BookGenre.WORK))
        repository.addBook(createTestNote("Personal book", category = BookGenre.PERSONAL))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteBookUseCase = deleteBookUseCase,
            repository = repository
        )
        
        vm.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            skipItems(1) // Initial success
            
            vm.onCategorySelected(BookGenre.WORK)
            advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).books.size)
            assertEquals(BookGenre.WORK, state.books.first().category)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `togglePin should toggle book pin status`() = runTest {
        val noteId = repository.addBook(createTestNote("Pin Me"))
        
        viewModel.togglePin(noteId)
        advanceUntilIdle()
        
        repository.getBookById(noteId).test {
            val book = awaitItem()
            assertTrue(book?.isPinned == true)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `deleteBook should remove book`() = runTest {
        val noteId = repository.addBook(createTestNote("Delete Me"))
        
        viewModel.deleteBook(noteId)
        advanceUntilIdle()
        
        repository.getAllNotes().test {
            val books = awaitItem()
            assertTrue(books.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    private fun createTestNote(
        title: String,
        category: BookGenre = BookGenre.GENERAL
    ): Book {
        return Book(
            id = 0,
            title = title,
            content = "Test content",
            category = category,
            color = BookRating.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
