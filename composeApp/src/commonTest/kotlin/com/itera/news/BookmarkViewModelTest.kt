package com.itera.news

import app.cash.turbine.test
import com.itera.news.domain.model.Article
import com.itera.news.presentation.screens.bookmark.BookmarkUiState
import com.itera.news.presentation.screens.bookmark.BookmarkViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {

    private lateinit var viewModel: BookmarkViewModel
    private lateinit var fakeRepository: FakeNewsRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeNewsRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `inisialisasi awal mengembalikan state Empty jika tidak ada bookmark`() = runTest {
        viewModel = BookmarkViewModel(fakeRepository)
        
        viewModel.uiState.test {
            // State awal bisa Loading atau langsung Empty karena flow statis
            val state = awaitItem()
            assertTrue(state is BookmarkUiState.Loading || state is BookmarkUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state menjadi Success jika repository memiliki data bookmark`() = runTest {
        val dummyArticle = Article("Judul", "Desc", "url1", "img", "date", "source", "Pro")
        fakeRepository.bookmarkedArticlesFlow.value = listOf(dummyArticle)
        
        viewModel = BookmarkViewModel(fakeRepository)

        viewModel.uiState.test {
            val state = awaitItem()
            if (state is BookmarkUiState.Loading) {
                val nextState = awaitItem()
                assertTrue(nextState is BookmarkUiState.Success)
                assertEquals(1, (nextState as BookmarkUiState.Success).articles.size)
            } else {
                assertTrue(state is BookmarkUiState.Success)
                assertEquals(1, (state as BookmarkUiState.Success).articles.size)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteArticle menghapus data dari repository`() = runTest {
        val dummyArticle = Article("Judul", "Desc", "url1", "img", "date", "source", "Pro")
        fakeRepository.bookmarkedArticlesFlow.value = listOf(dummyArticle)
        viewModel = BookmarkViewModel(fakeRepository)

        viewModel.deleteArticle(dummyArticle)

        // Menunggu coroutine penghapusan selesai
        testDispatcher.scheduler.advanceUntilIdle()

        // Verifikasi bahwa data di repository sekarang kosong
        assertTrue(fakeRepository.bookmarkedArticlesFlow.value.isEmpty())
    }
}