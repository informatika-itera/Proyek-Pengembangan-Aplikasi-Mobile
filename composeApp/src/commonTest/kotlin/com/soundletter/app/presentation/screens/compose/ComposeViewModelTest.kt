package com.soundletter.app.presentation.screens.compose

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.MusicTrack
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.MusicRepository
import com.soundletter.app.core.network.GeminiService
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.Json
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ComposeViewModelTest {

    private lateinit var viewModel: ComposeViewModel
    private lateinit var fakeLetterRepo: FakeLetterRepository
    private lateinit var fakeMusicRepo: FakeMusicRepository
    private lateinit var geminiService: GeminiService

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeLetterRepo = FakeLetterRepository()
        fakeMusicRepo = FakeMusicRepository()

        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel("""{"candidates": [{"content": {"parts": [{"text": "chill lofi"}]}}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val mockHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        geminiService = GeminiService(mockHttpClient)

        viewModel = ComposeViewModel(fakeLetterRepo, fakeMusicRepo, geminiService)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateInputState works correctly`() = runTest {
        viewModel.onRecipientChange("Gian")
        viewModel.onSenderChange("Sender")
        viewModel.onMessageChange("Hello")

        val state = viewModel.state.value
        assertEquals("Gian", state.recipient)
        assertEquals("Sender", state.sender)
        assertEquals("Hello", state.message)
    }

    @Test
    fun `sendSoundLetter success flow transitions correctly`() = runTest {
        viewModel.onRecipientChange("Gian")
        viewModel.onMessageChange("Hello")

        viewModel.state.test {
            // 1. Tangkap initial state dulu
            val initial = awaitItem()
            assertEquals(UiState.Idle, initial.sendStatus)

            // 2. Panggil fungsi SETELAH awaitItem pertama
            viewModel.sendSoundLetter()

            // 3. Tangkap Loading & Success
            assertIs<UiState.Loading>(awaitItem().sendStatus)

            val successState = awaitItem().sendStatus
            assertIs<UiState.Success<Boolean>>(successState)
            assertTrue((successState as UiState.Success).data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendSoundLetter returns Error when input is empty`() = runTest {
        viewModel.state.test {
            awaitItem() // initial

            viewModel.sendSoundLetter()

            val errorState = awaitItem().sendStatus
            assertIs<UiState.Error>(errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendSoundLetter returns Error when internet is offline`() = runTest {
        fakeLetterRepo.shouldFail = true
        viewModel.onRecipientChange("Gian")
        viewModel.onMessageChange("Hello")

        viewModel.state.test {
            awaitItem() // initial

            viewModel.sendSoundLetter()

            assertIs<UiState.Loading>(awaitItem().sendStatus)

            val errorState = awaitItem().sendStatus
            assertIs<UiState.Error>(errorState)
            assertEquals("Network Error", (errorState as UiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recommendSongs success flow updates suggestions`() = runTest {
        viewModel.onMessageChange("Aku lagi santai")

        viewModel.state.test {
            awaitItem() // initial
            viewModel.recommendSongs()

            assertTrue(awaitItem().isAiLoading)

            val successState = awaitItem()
            assertFalse(successState.isAiLoading)
            assertEquals(1, successState.suggestions.size)
            assertEquals("Jamendo Track", successState.suggestions.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test onSongSelect updates state`() = runTest {
        val song = SongSuggestion("Title", "Artist")
        viewModel.onSongSelect(song)
        assertEquals(song, viewModel.state.value.selectedSong)
    }

    @Test
    fun `test resetStatus returns to Idle`() = runTest {
        viewModel.onRecipientChange("A")
        viewModel.onMessageChange("B")
        viewModel.sendSoundLetter()

        advanceUntilIdle()

        viewModel.resetStatus()
        assertIs<UiState.Idle>(viewModel.state.value.sendStatus)
    }

    // FAKES
    class FakeLetterRepository : LetterRepository {
        var shouldFail = false
        override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
        override fun getGlobalLetters(): Flow<List<Note>> = flowOf(emptyList())
        override fun searchLetters(query: String): Flow<List<Note>> = flowOf(emptyList())
        override suspend fun sendLetter(letter: Note): Boolean {
            if (shouldFail) throw Exception("Network Error")
            return true
        }
        override suspend fun getLetterById(id: Long): Note? = null
        override suspend fun deleteLetter(id: Long) {}
        override suspend fun clearHistory() {}
    }

    class FakeMusicRepository : MusicRepository {
        override suspend fun searchSongs(query: String): List<MusicTrack> {
            return listOf(MusicTrack("Jamendo Track", "Artist", "url", "img"))
        }
    }
}