package id.pusakakata.ui.screens.addedit

import id.pusakakata.data.repository.FakeItemRepository
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
}
