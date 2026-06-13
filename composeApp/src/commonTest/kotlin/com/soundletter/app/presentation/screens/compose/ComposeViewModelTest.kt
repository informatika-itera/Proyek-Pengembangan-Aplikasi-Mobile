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

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeLetterRepo = FakeLetterRepository()
        fakeMusicRepo = FakeMusicRepository()

        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel("""{"candidates": [{"content": {"parts": [{"text": "happy pop"}]}}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val mockHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        geminiService = GeminiService(mockHttpClient, "fake_key")
        viewModel = ComposeViewModel(fakeLetterRepo, fakeMusicRepo, geminiService)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateInputState works correctly`() {
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
            assertEquals(UiState.Idle, awaitItem().sendStatus)
            viewModel.sendSoundLetter()
            assertIs<UiState.Loading>(awaitItem().sendStatus)
            val successState = awaitItem().sendStatus
            assertIs<UiState.Success<Boolean>>(successState)
            assertTrue((successState as UiState.Success).data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendSoundLetter error when empty recipient or message`() = runTest {
        viewModel.onRecipientChange("")
        viewModel.onMessageChange("")
        viewModel.sendSoundLetter()

        val state = viewModel.state.value
        assertIs<UiState.Error>(state.sendStatus)
        assertEquals("Penerima dan pesan tidak boleh kosong", (state.sendStatus as UiState.Error).message)
    }

    @Test
    fun `sendSoundLetter handles exception correctly`() = runTest {
        viewModel.onRecipientChange("Gian")
        viewModel.onMessageChange("Hello")
        fakeLetterRepo.shouldFail = true

        viewModel.state.test {
            awaitItem() // Initial Idle
            viewModel.sendSoundLetter()
            assertIs<UiState.Loading>(awaitItem().sendStatus)
            val errorState = awaitItem().sendStatus
            assertIs<UiState.Error>(errorState)
            assertEquals("Network Error", (errorState as UiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recommendSongs success flow updates suggestions using mood tags`() = runTest {
        viewModel.onMessageChange("Hari ini cerah sekali!")
        viewModel.state.test {
            awaitItem() // initial state
            viewModel.recommendSongs()
            assertTrue(awaitItem().isAiLoading)
            val resultState = awaitItem()
            assertFalse(resultState.isAiLoading)
            assertEquals("happy pop", fakeMusicRepo.lastMoodSearched)
            assertEquals(1, resultState.suggestions.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recommendSongs error when message is blank`() = runTest {
        viewModel.onMessageChange("")
        viewModel.uiEvent.test {
            viewModel.recommendSongs()
            val event = awaitItem()
            assertIs<ComposeUiEvent.ShowError>(event)
            assertEquals("Tulis pesanmu dulu!", event.message)
        }
    }

    @Test
    fun `recommendSongs handles empty music results`() = runTest {
        viewModel.onMessageChange("Sad")
        fakeMusicRepo.returnEmpty = true
        viewModel.uiEvent.test {
            viewModel.recommendSongs()
            val event = awaitItem()
            assertIs<ComposeUiEvent.ShowError>(event)
            assertEquals("Gagal memuat lagu. Coba lagi nanti.", event.message)
        }
    }

    @Test
    fun `recommendSongs handles exception from services`() = runTest {
        // Pemicu catch block di ViewModel melalui MusicRepository 
        // karena GeminiService menangkap exception secara internal.
        fakeMusicRepo.shouldFail = true
        viewModel.onMessageChange("Test")
        viewModel.uiEvent.test {
            viewModel.recommendSongs()
            val event = awaitItem()
            assertIs<ComposeUiEvent.ShowError>(event)
            assertTrue(event.message.contains("Koneksi API bermasalah"))
        }
    }

    @Test
    fun `onSongSelect updates state correctly`() {
        val song = SongSuggestion("Title", "Artist")
        viewModel.onSongSelect(song)
        assertEquals(song, viewModel.state.value.selectedSong)
    }

    @Test
    fun `test resetStatus returns to Idle`() {
        viewModel.onRecipientChange("A")
        viewModel.onMessageChange("B")
        viewModel.sendSoundLetter()
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
            kotlinx.coroutines.yield()
            if (shouldFail) throw Exception("Network Error")
            return true
        }
        override suspend fun getLetterById(id: Long): Note? = null
        override suspend fun deleteLetter(id: Long) : Unit {}
        override suspend fun clearHistory() : Unit {}
    }

    class FakeMusicRepository : MusicRepository {
        var lastMoodSearched: String? = null
        var returnEmpty = false
        var shouldFail = false
        override suspend fun searchSongs(mood: String): List<MusicTrack> {
            if (shouldFail) throw Exception("Music API Error")
            this.lastMoodSearched = mood
            if (returnEmpty) return emptyList()
            return listOf(MusicTrack("Mood Track", "Mood Artist", "url", "img"))
        }
    }
}
