package com.example.arcane.presentation

import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.presentation.screens.bookdetail.BookDetailEvent
import com.example.arcane.presentation.screens.bookdetail.BookDetailUiState
import com.example.arcane.presentation.screens.bookdetail.BookDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeBookRepository
    private lateinit var fakeFolderRepository: FakeFolderRepository
    private lateinit var viewModel: BookDetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeBookRepository()
        fakeFolderRepository = FakeFolderRepository()
        viewModel = BookDetailViewModel(fakeRepository, fakeFolderRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        assertIs<BookDetailUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun `loadBook with valid localId should emit Success`() = runTest {
        val book = createDummyBook(1L)
        fakeRepository.setBooks(listOf(book))

        // Collect di backgroundScope supaya infinite flow tidak block
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.loadBook("google_1", localId = 1L)
        advanceUntilIdle()

        assertIs<BookDetailUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun `loadBook with invalid localId and existing googleId should emit Success`() = runTest {
        val book = createDummyBook(1L, googleBookId = "google_abc")
        fakeRepository.setBooks(listOf(book))

        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.loadBook("google_abc", localId = 0L)
        advanceUntilIdle()

        assertIs<BookDetailUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun `loadBook with unknown googleId and no remote should emit Error`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.loadBook("unknown_id", localId = 0L)
        advanceUntilIdle()

        assertIs<BookDetailUiState.Error>(viewModel.uiState.value)
    }

    @Test
    fun `updateStatus should not crash and state stays Success`() = runTest {
        val book = createDummyBook(1L)
        fakeRepository.setBooks(listOf(book))

        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.loadBook("google_1", localId = 1L)
        advanceUntilIdle()

        viewModel.updateStatus(ReadingStatus.READING)
        advanceUntilIdle()

        assertIs<BookDetailUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun `deleteFromLibrary should emit BookDeleted event`() = runTest {
        val book = createDummyBook(1L)
        fakeRepository.setBooks(listOf(book))

        backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.loadBook("google_1", localId = 1L)
        advanceUntilIdle()
        assertIs<BookDetailUiState.Success>(viewModel.uiState.value)

        val events = mutableListOf<BookDetailEvent>()
        val eventJob = launch { viewModel.events.collect { events.add(it) } }
        advanceUntilIdle() // pastikan eventJob coroutine sudah start

        viewModel.deleteFromLibrary()
        advanceUntilIdle()

        assertTrue(events.any { it is BookDetailEvent.BookDeleted })
        eventJob.cancel()
    }

    @Test
    fun `saveNotesAndRating should emit ShowSnackbar event`() = runTest {
        val book = createDummyBook(1L)
        fakeRepository.setBooks(listOf(book))

        backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.loadBook("google_1", localId = 1L)
        advanceUntilIdle()
        assertIs<BookDetailUiState.Success>(viewModel.uiState.value)

        val events = mutableListOf<BookDetailEvent>()
        val eventJob = launch { viewModel.events.collect { events.add(it) } }
        advanceUntilIdle()

        viewModel.saveNotesAndRating("Buku bagus", 5)
        advanceUntilIdle()

        assertTrue(events.any { it is BookDetailEvent.ShowSnackbar })
        eventJob.cancel()
    }

    @Test
    fun `saveToLibrary should emit ShowSnackbar with success message`() = runTest {
        val book = createDummyBook(99L)

        val events = mutableListOf<BookDetailEvent>()
        val eventJob = launch { viewModel.events.collect { events.add(it) } }
        advanceUntilIdle() // pastikan collector aktif dulu

        viewModel.saveToLibrary(book)
        advanceUntilIdle()

        val snackbar = events.filterIsInstance<BookDetailEvent.ShowSnackbar>().firstOrNull()
        assertTrue(snackbar != null)
        assertTrue(snackbar.message.contains("berhasil"))
        eventJob.cancel()
    }
}
