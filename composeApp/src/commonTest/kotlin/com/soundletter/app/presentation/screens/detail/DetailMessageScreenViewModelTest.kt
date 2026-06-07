package com.soundletter.app.presentation.screens.detail

import app.cash.turbine.test
import com.soundletter.app.core.audio.AudioPlayer
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeDetailRepository : LetterRepository {
    var shouldFail = false
    override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
    override fun getGlobalLetters(): Flow<List<Note>> = flowOf(emptyList())
    override fun searchLetters(query: String): Flow<List<Note>> = flowOf(emptyList())

    override suspend fun getLetterById(id: Long): Note? {
        if (shouldFail) throw Exception("Network Error")
        return if (id == 1L) Note(id = 1L, recipient = "Test", content = "Content") else null
    }
    
    override suspend fun sendLetter(letter: Note): Boolean = true
    override suspend fun deleteLetter(id: Long) {}
    override suspend fun clearHistory() {}
}

class FakeAudioPlayer : AudioPlayer {
    var isPlayingStatus = false
    override fun play(url: String, onFinished: () -> Unit) { isPlayingStatus = true }
    override fun pause() { isPlayingStatus = false }
    override fun stop() { isPlayingStatus = false }
    override fun isPlaying(): Boolean = isPlayingStatus
    override fun release() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class DetailMessageScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeDetailRepository
    private lateinit var fakeAudioPlayer: FakeAudioPlayer
    private lateinit var viewModel: DetailMessageScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeDetailRepository()
        fakeAudioPlayer = FakeAudioPlayer()
        viewModel = DetailMessageScreenViewModel(repository, fakeAudioPlayer)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMessage with valid ID should emit Success`() = runTest {
        viewModel.state.test {
            // Check initial state
            assertEquals(UiState.Idle, awaitItem().letterState)
            
            viewModel.loadMessage("1")
            
            // Handle loading and success states
            var state = awaitItem()
            if (state.letterState is UiState.Loading) {
                state = awaitItem()
            }
            
            assertIs<UiState.Success<Note>>(state.letterState)
            assertEquals(1L, (state.letterState as UiState.Success).data.id)
        }
    }

    @Test
    fun `loadMessage with invalid ID should emit Error`() = runTest {
        viewModel.state.test {
            awaitItem() // initial
            viewModel.loadMessage("99")
            
            var state = awaitItem()
            if (state.letterState is UiState.Loading) {
                state = awaitItem()
            }
            
            assertIs<UiState.Error>(state.letterState)
            assertEquals("Letter not found", (state.letterState as UiState.Error).message)
        }
    }

    @Test
    fun `toggleAudio should update isPlaying state`() = runTest {
        viewModel.state.test {
            awaitItem() // initial
            viewModel.toggleAudio("https://test.com/audio.mp3")
            
            assertEquals(true, awaitItem().isPlaying)
            
            viewModel.toggleAudio("https://test.com/audio.mp3")
            assertEquals(false, awaitItem().isPlaying)
        }
    }
}
