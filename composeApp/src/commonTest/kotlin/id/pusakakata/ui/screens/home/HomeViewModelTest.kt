package id.pusakakata.ui.screens.home

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
    fun searchFiltering_worksCorrectly() = runTest {
        repository.insertWord(Word("1", "Apple", "Def1", "Cat"))
        repository.insertWord(Word("2", "Banana", "Def2", "Cat"))
        advanceUntilIdle()

        viewModel.onSearchQueryChange("app")
        advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(1, state.words.size)
        assertEquals("Apple", state.words[0].term)
    }
}
