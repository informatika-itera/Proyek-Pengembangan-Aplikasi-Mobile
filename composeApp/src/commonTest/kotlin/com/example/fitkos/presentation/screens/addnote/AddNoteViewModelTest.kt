package com.example.fitkos.presentation.screens.addnote

import app.cash.turbine.test
import com.example.fitkos.data.repository.FakeNoteRepository
import com.example.fitkos.domain.model.Note
import com.example.fitkos.domain.model.NoteCategory
import com.example.fitkos.domain.model.NoteColor
import com.example.fitkos.domain.usecase.SaveNoteUseCase
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
class AddNoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var viewModel: AddNoteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        saveNoteUseCase = SaveNoteUseCase(repository)
        viewModel = AddNoteViewModel(repository, saveNoteUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.title)
            assertEquals("", state.price)
            assertEquals(NoteCategory.LUNCH, state.category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTitleChange should update title in state`() = runTest {
        viewModel.onTitleChange("Nasi Goreng")
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Nasi Goreng", state.title)
        }
    }

    @Test
    fun `saveNote should fail if title is blank`() = runTest {
        viewModel.onTitleChange("")
        viewModel.saveNote()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Nama makanan wajib diisi", state.titleError)
        }
    }

    @Test
    fun `saveNote should succeed if title is valid`() = runTest {
        viewModel.onTitleChange("Sate Ayam")
        viewModel.onPriceChange("15000")
        
        viewModel.events.test {
            viewModel.saveNote()
            advanceUntilIdle()
            
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.NoteSaved)
        }
        
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertTrue(notes[0].title == "Sate Ayam")
            assertTrue(notes[0].content.contains("15000"))
        }
    }

    @Test
    fun `loadNote should populate state`() = runTest {
        val noteId = repository.insertNote(
            Note(
                id = 0,
                title = "Existing Note",
                content = "Harga: Rp20000\nDelicious lunch",
                category = NoteCategory.LUNCH,
                color = NoteColor.BLUE,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )
        
        viewModel.loadNote(noteId)
        advanceUntilIdle()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Existing Note", state.title)
            assertEquals("20000", state.price)
            assertEquals("Delicious lunch", state.content)
            assertEquals(NoteCategory.LUNCH, state.category)
            assertTrue(state.isEditMode)
        }
    }
}
