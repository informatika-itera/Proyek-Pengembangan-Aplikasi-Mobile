package id.pusakakata.ui.screens.favorite

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: FavoriteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
        viewModel = FavoriteViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadFavorites_empty_showsEmpty() = runTest {
        advanceUntilIdle()
        assertEquals(FavoriteUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun loadFavorites_withData_showsSuccess() = runTest {
        repository.insertWord(Word("1", "T1", "D1", "C1", isFavorite = true))
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is FavoriteUiState.Success)
        assertEquals(1, state.words.size)
    }

    @Test
    fun toggleFavorite_removesFromState() = runTest {
        repository.insertWord(Word("1", "T1", "D1", "C1", isFavorite = true))
        advanceUntilIdle()
        
        viewModel.toggleFavorite("1")
        advanceUntilIdle()
        
        assertEquals(FavoriteUiState.Empty, viewModel.uiState.value)
    }
}
