package id.pusakakata.presentation.screens.home

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
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
    fun loadWords_empty_updatesToEmptyState() = runTest {
        advanceUntilIdle()
        assertEquals(HomeUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun loadWords_withData_updatesToSuccessState() = runTest {
        repository.insertWord(Word("1", "Term", "Def", "Cat"))
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(1, state.words.size)
    }

    @Test
    fun deleteWord_updatesState() = runTest {
        repository.insertWord(Word("1", "Term", "Def", "Cat"))
        advanceUntilIdle()
        
        viewModel.deleteWord("1")
        advanceUntilIdle()
        
        assertEquals(HomeUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun executeSearch_newWord_callsRepository() = runTest {
        var successWord: Word? = null
        viewModel.onSearchQueryChange("Membiru")
        viewModel.executeSearch { successWord = it }
        
        // Wait for delay(1500)
        testDispatcher.scheduler.advanceTimeBy(1600)
        advanceUntilIdle()
        
        assertNotNull(successWord)
        assertEquals("Membiru", successWord?.term)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun executeSearch_existingWord_returnsDirectly() = runTest {
        val word = Word("1", "Pusaka", "Def", "Cat")
        repository.insertWord(word)
        advanceUntilIdle()
        
        var successWord: Word? = null
        viewModel.onSearchQueryChange("Pusaka")
        viewModel.executeSearch { successWord = it }
        
        advanceUntilIdle()
        assertEquals(word, successWord)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun toggleFavorite_updatesRepository() = runTest {
        val word = Word("1", "Pusaka", "Def", "Cat", isFavorite = false)
        repository.insertWord(word)
        advanceUntilIdle()
        
        viewModel.toggleFavorite("1")
        advanceUntilIdle()
        
        val updatedWord = repository.getWordById("1")
        assertTrue(updatedWord?.isFavorite == true)
    }

    @Test
    fun onSearchQueryChange_filtersWords() = runTest {
        repository.insertWord(Word("1", "Sasmita", "Isyarat", "Cat"))
        repository.insertWord(Word("2", "Meraki", "Jiwa", "Cat"))
        advanceUntilIdle()

        viewModel.onSearchQueryChange("Sas")
        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(1, state.words.size)
        assertEquals("Sasmita", state.words.first().term)
    }
}
