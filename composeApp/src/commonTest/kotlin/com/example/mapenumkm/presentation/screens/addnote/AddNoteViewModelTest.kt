package com.example.mapenumkm.presentation.screens.addnote

import app.cash.turbine.test
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.usecase.SaveNoteUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {

    private val repository: NoteRepository = mockk()
    private val saveNoteUseCase: SaveNoteUseCase = mockk()
    private lateinit var viewModel: AddNoteViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddNoteViewModel(repository, saveNoteUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNote updates state correctly`() = runTest {
        val note = Note(id = 1, title = "T", content = "C", price = 1.0, stock = 1, category = NoteCategory.FOOD, createdAt = Clock.System.now())
        every { repository.getNoteById(1) } returns flowOf(note)
        
        viewModel.loadNote(1)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("T", state.title)
        assertTrue(state.isEditMode)
    }

    @Test
    fun `onPriceChange updates only valid decimal numbers`() {
        viewModel.onPriceChange("100.5")
        assertEquals("100.5", viewModel.uiState.value.price)
        
        viewModel.onPriceChange("abc")
        assertEquals("100.5", viewModel.uiState.value.price)
    }

    @Test
    fun `saveNote success emits NoteSaved event`() = runTest {
        viewModel.onTitleChange("Title")
        coEvery { saveNoteUseCase(any()) } returns Result.success(1L)
        
        viewModel.events.test {
            viewModel.saveNote()
            assertEquals(AddNoteEvent.NoteSaved, awaitItem())
        }
    }

    @Test
    fun `saveNote failure emits Error event`() = runTest {
        viewModel.onTitleChange("Title")
        coEvery { saveNoteUseCase(any()) } returns Result.failure<Long>(Exception("Error"))
        
        viewModel.events.test {
            viewModel.saveNote()
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.Error)
            assertEquals("Error", event.message)
        }
    }
}
