package id.pusakakata.presentation.screens.detail

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: DetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadWord_existingId_showsSuccess() = runTest {
        val word = Word("1", "T1", "D1", "C1")
        repository.insertWord(word)
        
        viewModel = DetailViewModel(repository, "1")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Success)
        assertEquals("T1", (state as DetailUiState.Success).word.term)
    }

    @Test
    fun loadWord_nonExistingId_showsError() = runTest {
        viewModel = DetailViewModel(repository, "99")
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Error)
    }
}
