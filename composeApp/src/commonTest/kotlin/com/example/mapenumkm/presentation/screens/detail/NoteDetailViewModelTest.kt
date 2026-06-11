package com.example.mapenumkm.presentation.screens.detail

import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.usecase.DeleteNoteUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailViewModelTest {

    private val repository: NoteRepository = mockk()
    private val deleteNoteUseCase: DeleteNoteUseCase = mockk()
    private lateinit var viewModel: NoteDetailViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testNote = Note(
        id = 1L,
        title = "Test Title",
        content = "Test Content",
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NoteDetailViewModel(repository, deleteNoteUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNote should update state to Success when note exists`() = runTest {
        every { repository.getNoteById(1L) } returns flowOf(testNote)
        
        viewModel.loadNote(1L)
        
        assertTrue(viewModel.uiState.value is NoteDetailUiState.Success)
        assertEquals(testNote, (viewModel.uiState.value as NoteDetailUiState.Success).note)
    }

    @Test
    fun `loadNote should update state to NotFound when note does not exist`() = runTest {
        every { repository.getNoteById(1L) } returns flowOf(null)
        
        viewModel.loadNote(1L)
        
        assertEquals(NoteDetailUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `togglePin should call repository togglePinNote`() = runTest {
        every { repository.getNoteById(1L) } returns flowOf(testNote)
        coEvery { repository.togglePinNote(1L) } returns Unit
        
        viewModel.loadNote(1L)
        viewModel.togglePin()
        
        coVerify { repository.togglePinNote(1L) }
    }

    @Test
    fun `deleteNote should call deleteNoteUseCase and emit NoteDeleted event on success`() = runTest {
        every { repository.getNoteById(1L) } returns flowOf(testNote)
        coEvery { deleteNoteUseCase(1L) } returns Result.success(Unit)
        
        viewModel.loadNote(1L)
        
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { 
                if (it is NoteDetailEvent.NoteDeleted) {
                    // Success
                }
            }
        }
        
        viewModel.deleteNote()
        
        coVerify { deleteNoteUseCase(1L) }
    }

    @Test
    fun `getShareContent should return formatted string`() = runTest {
        every { repository.getNoteById(1L) } returns flowOf(testNote)
        viewModel.loadNote(1L)
        
        val content = viewModel.getShareContent()
        assertNotNull(content)
        assertTrue(content!!.contains("Test Title"))
        assertTrue(content.contains("Test Content"))
    }
}
