package id.pusakakata.ui.screens.addedit

import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.*

class FakeAddEditRepository : WordRepository {
    var savedWord: Word? = null
    override fun getAllWords(): Flow<List<Word>> = emptyFlow()
    override suspend fun getWordById(id: String): Word? = null
    override suspend fun insertWord(word: Word) { savedWord = word }
    override suspend fun updateWord(word: Word) { savedWord = word }
    override suspend fun deleteWord(id: String) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeAddEditRepository
    private lateinit var viewModel: AddEditViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeAddEditRepository()
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
    fun saveWord_updatesSuccess() = runTest(testDispatcher) {
        viewModel.onTermChange("Test")
        viewModel.onDefinitionChange("Def")
        viewModel.saveWord()
        
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSuccess)
        assertNotNull(repository.savedWord)
        assertEquals("Test", repository.savedWord?.term)
    }
}
