package com.example.mapenumkm.presentation.screens.product

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.usecase.DeleteNoteUseCase
import com.example.mapenumkm.domain.usecase.GetAllNotesUseCase
import com.example.mapenumkm.domain.usecase.SearchNotesUseCase
import com.example.mapenumkm.presentation.screens.home.HomeViewModel
import com.example.mapenumkm.presentation.theme.MaPenTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import com.example.mapenumkm.domain.repository.NoteRepository
import org.junit.Rule
import org.junit.Test

class ProductListUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchFunctionality() {
        val repository = FakeNoteRepository()
        val viewModel = HomeViewModel(
            GetAllNotesUseCase(repository),
            SearchNotesUseCase(repository),
            DeleteNoteUseCase(repository),
            repository
        )

        composeTestRule.setContent {
            MaPenTheme {
                ProductListScreen(
                    onNavigateToAddProduct = {},
                    onNavigateToEditProduct = {},
                    onNavigateToDashboard = {},
                    onNavigateToHistory = {},
                    onNavigateToReport = {},
                    viewModel = viewModel
                )
            }
        }

        // Add a product to repository
        repository.addNote(Note(id = 1, title = "Nasi Goreng", content = "Lezat", price = 15000.0, stock = 10, category = NoteCategory.FOOD))
        repository.addNote(Note(id = 2, title = "Es Teh", content = "Segar", price = 5000.0, stock = 20, category = NoteCategory.DRINK))

        // Find search bar and type
        composeTestRule.onNodeWithPlaceholderText("Cari produk...").performTextInput("Nasi")

        // Assert "Nasi Goreng" is visible and "Es Teh" is not
        composeTestRule.onNodeWithText("Nasi Goreng").assertIsDisplayed()
        composeTestRule.onNodeWithText("Es Teh").assertDoesNotExist()
    }
}

private class FakeNoteRepository : NoteRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    
    fun addNote(note: Note) {
        notes.value = notes.value + note
    }

    override fun getAllNotes(): Flow<List<Note>> = notes
    override fun getPinnedNotes(): Flow<List<Note>> = notes.map { it.filter { n -> n.isPinned } }
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> = notes.map { it.filter { n -> n.category == category } }
    override fun searchNotes(query: String): Flow<List<Note>> = notes.map { it.filter { n -> n.title.contains(query, true) } }
    override fun getNoteById(id: Long): Flow<Note?> = notes.map { it.find { n -> n.id == id } }
    override suspend fun insertNote(note: Note): Long = 0
    override suspend fun updateNote(note: Note) {}
    override suspend fun deleteNote(id: Long) {}
    override suspend fun togglePinNote(id: Long) {}
    override suspend fun deleteNotes(ids: List<Long>) {}
}
