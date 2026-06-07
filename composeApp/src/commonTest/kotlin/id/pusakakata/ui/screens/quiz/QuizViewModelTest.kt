package id.pusakakata.ui.screens.quiz

import id.pusakakata.data.repository.FakeItemRepository
import id.pusakakata.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: QuizViewModel

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
    fun initialState_emptyRepo_showsEmpty() = runTest {
        viewModel = QuizViewModel(repository)
        advanceUntilIdle()
        assertEquals(QuizUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun generateQuestion_withData_showsQuestion() = runTest {
        repository.insertWord(Word("1", "T1", "D1", "C1"))
        repository.insertWord(Word("2", "T2", "D2", "C1"))
        repository.insertWord(Word("3", "T3", "D3", "C1"))
        
        viewModel = QuizViewModel(repository)
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is QuizUiState.Question)
    }

    @Test
    fun submitCorrectAnswer_finishesWithCorrect() = runTest {
        val word = Word("1", "T1", "D1", "C1")
        repository.insertWord(word)
        repository.insertWord(Word("2", "T2", "D2", "C1"))
        repository.insertWord(Word("3", "T3", "D3", "C1"))
        
        viewModel = QuizViewModel(repository)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value as QuizUiState.Question
        viewModel.submitAnswer(state.correctAnswer)
        advanceUntilIdle()
        
        val finishedState = viewModel.uiState.value as QuizUiState.Finished
        assertTrue(finishedState.isCorrect)
    }
}
