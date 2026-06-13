package com.example.arcane.presentation

import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.domain.repository.BookRepository
import com.example.arcane.presentation.screens.home.HomeUiState
import com.example.arcane.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy

// ==================== TESTS ====================

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeBookRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeBookRepository()
        viewModel = HomeViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Helper: subscribe + advance melewati debounce + collect
    private suspend fun TestScope.collectAndAdvance() {
        val job = launch { viewModel.uiState.collect {} }
        advanceTimeBy(400) // lewati debounce 300ms
        advanceUntilIdle()
        job.cancel()
    }

    @Test
    fun `when no books exist, state should be Empty`() = runTest {
        fakeRepository.setBooks(emptyList())
        collectAndAdvance()
        assertIs<HomeUiState.Empty>(viewModel.uiState.value)
    }

    @Test
    fun `when books exist, state should be Success`() = runTest {
        fakeRepository.setBooks(listOf(createDummyBook(1L)))
        collectAndAdvance()
        assertIs<HomeUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun `when books exist, Success state contains correct number of books`() = runTest {
        fakeRepository.setBooks(listOf(createDummyBook(1L), createDummyBook(2L), createDummyBook(3L)))
        collectAndAdvance()
        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(3, state.books.size)
    }

    @Test
    fun `when search query matches book title, filtered books returned`() = runTest {
        fakeRepository.setBooks(listOf(
            createDummyBook(1L, title = "Kotlin Programming"),
            createDummyBook(2L, title = "Android Development"),
            createDummyBook(3L, title = "Kotlin Multiplatform")
        ))
        collectAndAdvance()
        viewModel.onSearchQueryChange("Kotlin")
        collectAndAdvance()
        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(2, state.books.size)
    }

    @Test
    fun `when filter by READING status, only reading books returned`() = runTest {
        fakeRepository.setBooks(listOf(
            createDummyBook(1L, status = ReadingStatus.READING),
            createDummyBook(2L, status = ReadingStatus.TO_READ),
            createDummyBook(3L, status = ReadingStatus.COMPLETED)
        ))
        collectAndAdvance()
        viewModel.onStatusSelected(ReadingStatus.READING)
        collectAndAdvance()
        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(1, state.books.size)
    }

    @Test
    fun `when search query does not match any book, state should be Empty`() = runTest {
        fakeRepository.setBooks(listOf(createDummyBook(1L, title = "Kotlin Programming")))
        collectAndAdvance()
        viewModel.onSearchQueryChange("Flutter")
        collectAndAdvance()
        assertIs<HomeUiState.Empty>(viewModel.uiState.value)
    }

    @Test
    fun `when book deleted, it should not appear in list`() = runTest {
        fakeRepository.setBooks(listOf(createDummyBook(1L), createDummyBook(2L)))
        collectAndAdvance()
        viewModel.deleteBook(1L)
        collectAndAdvance()
        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(1, state.books.size)
    }

    @Test
    fun `when status filter cleared, all books returned`() = runTest {
        fakeRepository.setBooks(listOf(
            createDummyBook(1L, status = ReadingStatus.READING),
            createDummyBook(2L, status = ReadingStatus.TO_READ)
        ))
        collectAndAdvance()
        viewModel.onStatusSelected(ReadingStatus.READING)
        collectAndAdvance()
        viewModel.onStatusSelected(null)
        collectAndAdvance()
        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(2, state.books.size)
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        // Jangan subscribe dulu — cek state awal sebelum ada collector
        assertIs<HomeUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun `when filter by COMPLETED status, only completed books returned`() = runTest {
        fakeRepository.setBooks(listOf(
            createDummyBook(1L, status = ReadingStatus.COMPLETED),
            createDummyBook(2L, status = ReadingStatus.COMPLETED),
            createDummyBook(3L, status = ReadingStatus.TO_READ)
        ))
        collectAndAdvance()
        viewModel.onStatusSelected(ReadingStatus.COMPLETED)
        collectAndAdvance()
        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(2, state.books.size)
    }
}