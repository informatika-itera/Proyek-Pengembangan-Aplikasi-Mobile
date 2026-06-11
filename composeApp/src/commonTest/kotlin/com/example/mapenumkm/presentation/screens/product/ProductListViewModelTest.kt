package com.example.mapenumkm.presentation.screens.product

import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.repository.NoteRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    private val repository: NoteRepository = mockk()
    private lateinit var viewModel: ProductListViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getAllNotes() } returns flowOf(emptyList())
        viewModel = ProductListViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty products`() {
        val state = viewModel.state.value
        assertTrue(state.products.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `onSearchQueryChange should update query in state`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        
        viewModel.onSearchQueryChange("test")
        assertEquals("test", viewModel.state.value.searchQuery)
    }

    @Test
    fun `onCategoryChange should toggle category`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        
        viewModel.onCategoryChange(NoteCategory.FOOD)
        assertEquals(NoteCategory.FOOD, viewModel.state.value.selectedCategory)
    }

    @Test
    fun `deleteProduct should call repository`() = runTest {
        val note = Note(id = 1L, title = "Test", content = "Desc", price = 10.0, stock = 5, category = NoteCategory.FOOD, createdAt = Clock.System.now())
        coEvery { repository.deleteNote(1L) } returns Unit
        
        viewModel.deleteProduct(note)
        advanceUntilIdle()
        
        coVerify { repository.deleteNote(1L) }
    }
}
