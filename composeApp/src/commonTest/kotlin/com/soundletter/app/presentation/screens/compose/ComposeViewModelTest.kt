package com.soundletter.app.presentation.screens.compose

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.MusicRepository
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
import kotlin.test.assertTrue

class FakeLetterRepo : LetterRepository {
    var shouldFail = false
    var wasSendCalled = false
    var lastDeletedId: Long? = null
    
    override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
    
    override fun getGlobalLetters(): Flow<List<Note>> = flowOf(emptyList())

    override suspend fun getLetterById(id: Long): Note? = null
    
    override suspend fun sendLetter(letter: Note) {
        wasSendCalled = true
        if (shouldFail) throw Exception("Network Error")
    }

    override suspend fun deleteLetter(id: Long) {
        lastDeletedId = id
    }

    override suspend fun clearHistory() {
        // No-op for fake
    }
}

class FakeMusicRepo : MusicRepository {
    override suspend fun searchSongs(query: String): List<String> = emptyList()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ComposeViewModelTest {
    private lateinit var viewModel: ComposeViewModel
    private lateinit var repository: FakeLetterRepo

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeLetterRepo()
        viewModel = ComposeViewModel(repository, FakeMusicRepo())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendSoundLetter should reach Success state when repository succeeds`() = runTest {
        viewModel.onRecipientChange("Dzakky")
        viewModel.onMessageChange("Hello")
        
        viewModel.sendSoundLetter()
        
        assertIs<UiState.Success<Unit>>(viewModel.state.value.sendStatus)
        assertTrue(repository.wasSendCalled)
    }

    @Test
    fun `sendSoundLetter should return error state when repository fails`() = runTest {
        repository.shouldFail = true
        viewModel.onRecipientChange("To")
        viewModel.onMessageChange("Msg")
        
        viewModel.sendSoundLetter()
        
        val status = viewModel.state.value.sendStatus
        assertIs<UiState.Error>(status)
        assertEquals("Network Error", (status as UiState.Error).message)
    }

    @Test
    fun `sendSoundLetter should return error when recipient is blank`() = runTest {
        viewModel.onRecipientChange("")
        viewModel.onMessageChange("Msg")
        
        viewModel.sendSoundLetter()
        
        val status = viewModel.state.value.sendStatus
        assertIs<UiState.Error>(status)
        assertEquals("Recipient and message cannot be empty", (status as UiState.Error).message)
        assertTrue(!repository.wasSendCalled)
    }

    @Test
    fun `onMessageChange should update state correctly`() = runTest {
        val message = "Hello World"
        viewModel.onMessageChange(message)
        assertEquals(message, viewModel.state.value.message)
    }

    @Test
    fun `onSenderChange should update state correctly`() = runTest {
        val sender = "John Doe"
        viewModel.onSenderChange(sender)
        assertEquals(sender, viewModel.state.value.sender)
    }

    @Test
    fun `onSongSelect should update selected song in state`() = runTest {
        val song = SongSuggestion("Starboy", "The Weeknd")
        viewModel.onSongSelect(song)
        assertEquals(song, viewModel.state.value.selectedSong)
    }

    @Test
    fun `recommendSongs should update suggestions`() = runTest {
        viewModel.state.test {
            awaitItem() // Initial state
            viewModel.recommendSongs()
            
            // Collect Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isAiLoading)
            
            // Collect Success state
            val resultState = awaitItem()
            assertTrue(resultState.suggestions.isNotEmpty())
            assertTrue(!resultState.isAiLoading)
        }
    }
}
