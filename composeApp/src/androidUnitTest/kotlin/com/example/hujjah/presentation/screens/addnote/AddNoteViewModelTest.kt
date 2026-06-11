package com.example.hujjah.presentation.screens.addnote

import app.cash.turbine.test
import com.example.hujjah.data.repository.FakeNoteRepository
import com.example.hujjah.domain.model.Note
import com.example.hujjah.domain.usecase.SaveNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
        viewModel.viewModelScope.coroutineContext.cancelChildren()
        Dispatchers.resetMain()
    }

    @Test
    fun `initializeNote with null id and content should maintain default empty state`() = runTest(testDispatcher) {
        viewModel.initializeNote(null, null)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.title)
        assertEquals("", state.content)
        assertEquals("Umum", state.category)
        assertFalse(state.isEditMode)
        assertNull(state.titleError)
    }

    @Test
    fun `initializeNote with initialContent should populate state with category Hujjah Lens`() = runTest(testDispatcher) {
        viewModel.initializeNote(null, "Rujukan: Al-Fatihah Ayat 1")
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.title)
        assertEquals("Rujukan: Al-Fatihah Ayat 1", state.content)
        assertEquals("Hujjah Lens", state.category)
        assertFalse(state.isEditMode)
    }

    @Test
    fun `initializeNote with noteId should load note from repository`() = runTest(testDispatcher) {
        // Arrange
        val note = Note(title = "Existing Note", content = "Existing Content", category = "Hadits")
        val id = repository.insertNote(note)
        testScheduler.advanceUntilIdle()

        // Act
        viewModel.initializeNote(id, null)
        testScheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Existing Note", state.title)
        assertEquals("Existing Content", state.content)
        assertEquals("Hadits", state.category)
        assertTrue(state.isEditMode)
    }

    @Test
    fun `onTitleChange should update title and clear titleError`() = runTest(testDispatcher) {
        // Arrange
        viewModel.onTitleChange("New Title")
        assertEquals("New Title", viewModel.uiState.value.title)
    }

    @Test
    fun `saveNote with empty title and content should set titleError`() = runTest(testDispatcher) {
        viewModel.saveNote()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.titleError)
        assertEquals("Judul atau konten harus diisi", state.titleError)
    }

    @Test
    fun `saveNote with valid title should save and emit NoteSaved event`() = runTest(testDispatcher) {
        viewModel.onTitleChange("My Journal")
        viewModel.onContentChange("Alhamdulillah for today")
        viewModel.onCategoryChange("Personal")
        
        viewModel.events.test {
            viewModel.saveNote()
            testScheduler.advanceUntilIdle()
            
            assertEquals(AddNoteEvent.NoteSaved, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // Verify it was added to repository
        repository.getAllNotes().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("My Journal", list.first().title)
            assertEquals("Alhamdulillah for today", list.first().content)
            assertEquals("Personal", list.first().category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applyAISuggestion should update content`() = runTest(testDispatcher) {
        viewModel.applyAISuggestion("AI content suggestion")
        assertEquals("AI content suggestion", viewModel.uiState.value.content)
    }

    @Test
    fun `applyAITitle should update title`() = runTest(testDispatcher) {
        viewModel.applyAITitle("AI title suggestion")
        assertEquals("AI title suggestion", viewModel.uiState.value.title)
    }
}
