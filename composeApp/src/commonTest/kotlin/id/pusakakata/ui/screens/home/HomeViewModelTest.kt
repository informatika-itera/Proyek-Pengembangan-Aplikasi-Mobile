package id.pusakakata.ui.screens.home

import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.*

class FakeWordRepository : WordRepository {
    private val words = mutableListOf<Word>()
    override fun getAllWords(): Flow<List<Word>> = flowOf(words)
    override suspend fun getWordById(id: String): Word? = words.find { it.id == id }
    override suspend fun insertWord(word: Word) { words.add(word) }
    override suspend fun updateWord(word: Word) { 
        val index = words.indexOfFirst { it.id == word.id }
        if (index != -1) words[index] = word
    }
    override suspend fun deleteWord(id: String) { words.removeAll { it.id == id } }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeWordRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWordRepository()
        viewModel = HomeViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isLoading() {
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun loadWords_empty_updatesToEmptyState() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertEquals(HomeUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun loadWords_withData_updatesToSuccessState() = runTest(testDispatcher) {
        repository.insertWord(Word("1", "Term", "Def", "Cat"))
        
        // Re-init to trigger collection
        val vm = HomeViewModel(repository)
        advanceUntilIdle()
        
        assertTrue(vm.uiState.value is HomeUiState.Success)
    }
}
