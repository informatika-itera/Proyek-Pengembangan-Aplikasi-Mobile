package id.pusakakata.ui.screens.flashcard

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.model.SRSData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: FlashcardViewModel

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
    fun initialState_empty_showsEmpty() = runTest {
        viewModel = FlashcardViewModel(repository)
        advanceUntilIdle()
        assertEquals(FlashcardUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun loadCards_dueCards_showsSuccess() = runTest {
        val word = Word("1", "T1", "D1", "C1", srsData = SRSData(nextReview = Clock.System.now()))
        repository.insertWord(word)
        
        viewModel = FlashcardViewModel(repository)
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is FlashcardUiState.Success)
    }

    @Test
    fun flipCard_updatesState() = runTest {
        val word = Word("1", "T1", "D1", "C1", srsData = SRSData(nextReview = Clock.System.now()))
        repository.insertWord(word)
        
        viewModel = FlashcardViewModel(repository)
        advanceUntilIdle()
        
        viewModel.flipCard()
        val state = viewModel.uiState.value as FlashcardUiState.Success
        assertTrue(state.isFlipped)
    }
}
