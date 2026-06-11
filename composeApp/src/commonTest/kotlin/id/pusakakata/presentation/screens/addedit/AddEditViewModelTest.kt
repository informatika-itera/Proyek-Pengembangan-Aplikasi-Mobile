package id.pusakakata.presentation.screens.addedit

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: AddEditViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
        viewModel = AddEditViewModel(repository, null)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isCorrect() {
        val state = viewModel.uiState.value
        assertEquals("", state.term)
        assertFalse(state.canSave)
    }

    @Test
    fun inputChanges_updatesState() {
        viewModel.onTermChange("Pusaka")
        viewModel.onDefinitionChange("Harta benda")
        
        val state = viewModel.uiState.value
        assertEquals("Pusaka", state.term)
        assertEquals("Harta benda", state.definition)
        assertTrue(state.canSave)
    }

    @Test
    fun saveWord_updatesSuccess() = runTest {
        viewModel.onTermChange("Test")
        viewModel.onDefinitionChange("Def")
        viewModel.saveWord()
        
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSuccess)
        
        val allWords = repository.getAllWords().first()
        val word = repository.getWordById(allWords.first().id)
        assertNotNull(word)
        assertEquals("Test", word.term)
    }

    @Test
    fun searchOnline_updatesDefinition() = runTest {
        viewModel.onTermChange("Sasmita")
        viewModel.searchOnline()
        
        advanceUntilIdle()
        assertEquals("AI Definition for Sasmita", viewModel.uiState.value.definition)
    }

    @Test
    fun editExistingWord_loadsInitialData() = runTest {
        val existingWord = Word("123", "Eka", "Cantik", "Umum")
        repository.insertWord(existingWord)
        
        val editViewModel = AddEditViewModel(repository, "123")
        advanceUntilIdle()
        
        val state = editViewModel.uiState.value
        assertEquals("Eka", state.term)
        assertEquals("Cantik", state.definition)
    }

    @Test
    fun onCategoryChange_updatesState() {
        viewModel.onCategoryChange("Sastra")
        assertEquals("Sastra", viewModel.uiState.value.category)
    }

    @Test
    fun onExampleChange_updatesState() {
        viewModel.onExampleChange("Ini contoh")
        assertEquals("Ini contoh", viewModel.uiState.value.example)
    }
}
