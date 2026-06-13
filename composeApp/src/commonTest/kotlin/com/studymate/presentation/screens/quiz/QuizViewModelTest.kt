package com.studymate.presentation.screens.quiz

import app.cash.turbine.test
import com.studymate.domain.model.Note
import com.studymate.domain.model.QuizQuestion
import com.studymate.domain.repository.AIRepository
import com.studymate.domain.repository.ActivityRepository
import com.studymate.domain.repository.QuizRepository
import com.studymate.data.repository.FakeNoteRepository
import com.studymate.domain.model.QuizHistory
import com.studymate.domain.model.ActivityDay
import com.studymate.data.remote.dto.QuizResponseDto
import com.studymate.data.remote.dto.QuizItemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var aiRepository: FakeAIRepository
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var quizRepository: FakeQuizRepository
    private lateinit var activityRepository: FakeActivityRepository
    private lateinit var viewModel: QuizViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        aiRepository = FakeAIRepository()
        noteRepository = FakeNoteRepository()
        quizRepository = FakeQuizRepository()
        activityRepository = FakeActivityRepository()

        viewModel = QuizViewModel(
            aiRepository = aiRepository,
            noteRepository = noteRepository,
            quizRepository = quizRepository,
            activityRepository = activityRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be History`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem() is QuizUiState.History)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startQuiz should change state to Loading then ActiveSession`() = runTest {
        val note = Note(id = 1, title = "Test Note", rawContent = "Content", subject = "Math", createdAt = 0, updatedAt = 0)
        noteRepository.insertNote(note)

        viewModel.uiState.test {
            // Initial state
            assertTrue(awaitItem() is QuizUiState.History)
            
            viewModel.startQuiz(note, 1)
            
            // Loading
            assertTrue(awaitItem() is QuizUiState.Loading)
            
            // ActiveSession
            val nextItem = awaitItem()
            assertTrue(nextItem is QuizUiState.ActiveSession, "Expected ActiveSession but got $nextItem")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `submitAnswer should update score and finish session when last question`() = runTest {
        val note = Note(id = 1, title = "Test Note", rawContent = "Content", subject = "Math", createdAt = 0, updatedAt = 0)
        noteRepository.insertNote(note)
        
        viewModel.uiState.test {
            assertTrue(awaitItem() is QuizUiState.History)
            
            viewModel.startQuiz(note, 1)
            assertTrue(awaitItem() is QuizUiState.Loading)
            
            assertTrue(awaitItem() is QuizUiState.ActiveSession)
            
            viewModel.submitAnswer(0, 0)
            
            // Expected transition: ActiveSession -> ActiveSession (isFinished = true)
            // Note: StateFlow might emit the intermediate update or skip to the finished one.
            var state = awaitItem()
            while (state is QuizUiState.ActiveSession && !state.isFinished) {
                state = awaitItem()
            }
            
            assertTrue(state is QuizUiState.ActiveSession)
            assertTrue((state as QuizUiState.ActiveSession).isFinished)

            cancelAndIgnoreRemainingEvents()
        }
    }
}

class FakeAIRepository : AIRepository {
    override suspend fun generateQuiz(subject: String, title: String, noteContent: String, questionCount: Int): Result<String> {
        delay(10) // Ensure suspension so Loading state is captured
        val quizResponse = QuizResponseDto(
            questions = List(questionCount) { i ->
                QuizItemDto(
                    question = "Question $i",
                    options = listOf("Correct", "Wrong 1", "Wrong 2", "Wrong 3"),
                    correct = 0,
                    explanation = "Because it is correct."
                )
            }
        )
        return Result.success(Json.encodeToString(quizResponse))
    }

    override suspend fun refineNote(subject: String, title: String, content: String): Result<String> = Result.success(content)
    override suspend fun generateMantra(): Result<String> = Result.success("Study hard!")
}

class FakeQuizRepository : QuizRepository {
    private val history = MutableStateFlow<List<QuizHistory>>(emptyList())
    override fun getAllHistory(): Flow<List<QuizHistory>> = history
    override suspend fun insertHistory(historyItem: QuizHistory) {
        history.update { it + historyItem }
    }
    override suspend fun deleteHistory(id: Long) {
        history.update { it.filter { h -> h.id != id } }
    }
}

class FakeActivityRepository : ActivityRepository {
    override fun getActivityHeatmap(days: Int): Flow<List<ActivityDay>> = flowOf(emptyList())
    override suspend fun recordQuizCompletion() {}
    override suspend fun recordNoteCreation() {}
    override suspend fun getMonthlyQuizCount(): Int = 0
}
