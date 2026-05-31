package com.itera.news

import app.cash.turbine.test
import com.itera.news.domain.model.Article
import com.itera.news.presentation.screens.add.AddEditUiState
import com.itera.news.presentation.screens.add.AddEditViewModel
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
class AddEditViewModelTest {

    private lateinit var viewModel: AddEditViewModel
    private lateinit var fakeRepository: FakeNewsRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeNewsRepository()
        viewModel = AddEditViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTitleChange memperbarui state title`() {
        viewModel.onTitleChange("Judul Baru")
        assertEquals("Judul Baru", viewModel.title.value)
    }

    @Test
    fun `onCategoryChange memperbarui state category`() {
        viewModel.onCategoryChange("Pro")
        assertEquals("Pro", viewModel.category.value)
    }

    @Test
    fun `saveArticle dengan judul kosong mengubah state menjadi Error`() = runTest {
        viewModel.onTitleChange("")
        viewModel.onDescriptionChange("Deskripsi")
        viewModel.saveArticle()
        assertTrue(viewModel.uiState.value is AddEditUiState.Error)
    }

    @Test
    fun `saveArticle dengan deskripsi kosong mengubah state menjadi Error`() = runTest {
        viewModel.onTitleChange("Judul")
        viewModel.onDescriptionChange("")
        viewModel.saveArticle()
        assertTrue(viewModel.uiState.value is AddEditUiState.Error)
    }

    @Test
    fun `saveArticle dengan data valid menyimpan ke repository dan mengubah state Success`() = runTest {
        viewModel.onTitleChange("Judul Valid")
        viewModel.onDescriptionChange("Deskripsi Valid")
        
        viewModel.saveArticle()
        testDispatcher.scheduler.advanceUntilIdle() 

        assertTrue(viewModel.uiState.value is AddEditUiState.Success)
        assertEquals(1, fakeRepository.bookmarkedArticlesFlow.value.size)
        assertEquals("Judul Valid", fakeRepository.bookmarkedArticlesFlow.value.first().title)
    }

    @Test
    fun `initArticle dengan URL valid memuat data artikel dari repository`() = runTest {
        val article = Article("Judul Edit", "Desc", "url1", "img", "date", "source", "Netral")
        fakeRepository.bookmarkedArticlesFlow.value = listOf(article)

        viewModel.initArticle("url1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Judul Edit", viewModel.title.value)
        assertEquals("Desc", viewModel.description.value)
        assertTrue(viewModel.uiState.value is AddEditUiState.Idle)
    }

    @Test
    fun `initArticle dengan URL yang tidak ditemukan mengubah state menjadi Error`() = runTest {
        viewModel.initArticle("url_ngawur")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AddEditUiState.Error)
    }
}