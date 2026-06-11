package com.example.hujjah.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.hujjah.data.repository.FakeNoteRepository
import com.example.hujjah.domain.model.Note
import com.example.hujjah.domain.model.islamic.*
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import com.example.hujjah.domain.usecase.*
import com.example.hujjah.presentation.screens.addnote.AddNoteScreen
import com.example.hujjah.presentation.screens.addnote.AddNoteViewModel
import com.example.hujjah.presentation.screens.notes.NotesScreen
import com.example.hujjah.presentation.screens.notes.NotesViewModel
import com.example.hujjah.presentation.theme.HujjahTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.cancelChildren
import androidx.lifecycle.viewModelScope
import kotlin.test.assertEquals
import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.hujjah.data.local.datastore.UserPreferences
import okio.Path.Companion.toPath
import kotlinx.datetime.Clock
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import java.util.concurrent.TimeUnit
import org.robolectric.shadows.ShadowLooper

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Koin tidak dimulai secara otomatis di sini
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = TestApplication::class)
class NoteUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var hujjahRepository: HujjahRepository
    private lateinit var userPreferences: UserPreferences

    class FakeHujjahRepository : HujjahRepository {
        override fun getTopics(): Flow<List<TopicOption>> = flowOf(emptyList())
        override fun getReferencesByTopic(topicId: String): Flow<List<IslamicReference>> = flowOf(emptyList())
        override fun getReferenceById(referenceId: String): Flow<IslamicReference?> = flowOf(null)
        override fun getSurahs(forceRefresh: Boolean): Flow<List<SurahItem>> = flowOf(
            listOf(SurahItem(1, "Al-Fatihah", "The Opening", 7, "Mekah", "alfatihah"))
        )
        override fun getSurahDetail(surahNumber: Int, surahName: String, forceRefresh: Boolean): Flow<List<VerseItem>> = flowOf(
            listOf(
                VerseItem(1, "بِسْمِ Lَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang")
            )
        )
        override fun getHadithBooks(forceRefresh: Boolean): Flow<List<HadithBookItem>> = flowOf(
            listOf(HadithBookItem("bukhari", "Shahih Bukhari", 7563))
        )
        override fun getHadithRange(bookId: String, start: Int, end: Int, forceRefresh: Boolean): Flow<List<HadithItem>> = flowOf(
            listOf(
                HadithItem(1, "إِنَّمَا الأَعْمَالُ بِالنِّIَّاتِ", "Sesungguhnya setiap amalan bergantung pada niatnya.")
            )
        )
        override fun getChatHistory(): Flow<List<ChatMessage>> = flowOf(emptyList())
        override suspend fun saveChatMessage(message: ChatMessage) {}
        override suspend fun deleteChatMessage(messageId: String) {}
        override suspend fun clearChatHistory() {}
    }

    @Before
    fun setup() {
        stopKoin() // Hentikan Koin sisa jika ada
        Dispatchers.setMain(testDispatcher)
        
        noteRepository = FakeNoteRepository()
        hujjahRepository = FakeHujjahRepository()

        // Buat file preference unik agar tidak bentrok
        val uniqueName = "test_hujjah_pref_ui_${Clock.System.now().toEpochMilliseconds()}_${(0..99999).random()}.preferences_pb"
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { ("build/tmp/" + uniqueName).toPath() }
        )
        userPreferences = UserPreferences(dataStore)

        // Mulai Koin khusus untuk injeksi di AddNoteScreen
        startKoin {
            modules(module {
                single { hujjahRepository }
                single { userPreferences }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun testNotesListAndSearch() = runTest(testDispatcher) {
        // Tambahkan catatan sampel
        noteRepository.insertNote(Note(title = "Nasihat Pagi", content = "Jangan lupa shalat dhuha.", category = "Ibadah"))
        noteRepository.insertNote(Note(title = "Keutamaan Sabar", content = "Sabar itu sebagian dari iman.", category = "Akhlaq"))

        val notesViewModel = NotesViewModel(
            repository = noteRepository,
            getAllNotesUseCase = GetAllNotesUseCase(noteRepository),
            searchNotesUseCase = SearchNotesUseCase(noteRepository),
            deleteNoteUseCase = DeleteNoteUseCase(noteRepository)
        )

        composeTestRule.setContent {
            HujjahTheme(darkTheme = false) {
                NotesScreen(
                    onNavigateBack = {},
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    viewModel = notesViewModel
                )
            }
        }

        // Majukan looper dan dispatcher virtual untuk memicu debounce 200ms
        testScheduler.advanceTimeBy(300)
        testScheduler.advanceUntilIdle()
        ShadowLooper.idleMainLooper(300, TimeUnit.MILLISECONDS)
        composeTestRule.waitForIdle()

        // Debug output
        println("--- DEBUG STATS ---")
        println("ViewModel Notes size: ${notesViewModel.uiState.value.notes.size}")
        println("ViewModel Notes: ${notesViewModel.uiState.value.notes.map { it.title }}")
        println("ViewModel Categories: ${notesViewModel.uiState.value.categories}")
        println("ViewModel IsLoading: ${notesViewModel.uiState.value.isLoading}")
        println("ViewModel SearchQuery: ${notesViewModel.uiState.value.searchQuery}")
        println("-------------------")

        // Verifikasi bahwa list menampilkan catatan awal
        composeTestRule.onNodeWithText("Nasihat Pagi").assertExists()

        // Ketik pencarian "Sabar"
        composeTestRule.onNodeWithText("Cari Catatan...").performTextInput("Sabar")
        testScheduler.advanceTimeBy(300)
        testScheduler.advanceUntilIdle()
        ShadowLooper.idleMainLooper(300, TimeUnit.MILLISECONDS)
        composeTestRule.waitForIdle()

        // Verifikasi list terfilter
        composeTestRule.onNodeWithText("Keutamaan Sabar").assertExists()
        composeTestRule.onNodeWithText("Nasihat Pagi").assertDoesNotExist()

        notesViewModel.viewModelScope.coroutineContext.cancelChildren()
    }

    @Test
    fun testCategoryFilterClick() = runTest(testDispatcher) {
        // Tambahkan catatan sampel
        noteRepository.insertNote(Note(title = "Nasihat Shalat", content = "Shalat tepat waktu.", category = "Ibadah"))
        noteRepository.insertNote(Note(title = "Adab Bicara", content = "Bicaralah yang baik.", category = "Adab"))

        val notesViewModel = NotesViewModel(
            repository = noteRepository,
            getAllNotesUseCase = GetAllNotesUseCase(noteRepository),
            searchNotesUseCase = SearchNotesUseCase(noteRepository),
            deleteNoteUseCase = DeleteNoteUseCase(noteRepository)
        )

        composeTestRule.setContent {
            HujjahTheme(darkTheme = false) {
                NotesScreen(
                    onNavigateBack = {},
                    onNavigateToAddNote = {},
                    onNavigateToDetail = {},
                    viewModel = notesViewModel
                )
            }
        }

        testScheduler.advanceTimeBy(300)
        testScheduler.advanceUntilIdle()
        ShadowLooper.idleMainLooper(300, TimeUnit.MILLISECONDS)
        composeTestRule.waitForIdle()

        // Klik chip kategori "Ibadah" (gunakan filter SemanticsProperties.Role agar tidak bentrok dengan card)
        composeTestRule.onNode(hasText("Ibadah") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)).performClick()
        testScheduler.advanceTimeBy(300)
        testScheduler.advanceUntilIdle()
        ShadowLooper.idleMainLooper(300, TimeUnit.MILLISECONDS)
        composeTestRule.waitForIdle()

        // Verifikasi bahwa hanya catatan ber-kategori "Ibadah" yang tampil
        composeTestRule.onNodeWithText("Nasihat Shalat").assertExists()
        composeTestRule.onNodeWithText("Adab Bicara").assertDoesNotExist()

        notesViewModel.viewModelScope.coroutineContext.cancelChildren()
    }

    @Test
    fun testAddNewNoteAndSave() = runTest(testDispatcher) {
        val addNoteViewModel = AddNoteViewModel(
            repository = noteRepository,
            saveNoteUseCase = SaveNoteUseCase(noteRepository)
        )

        composeTestRule.setContent {
            HujjahTheme(darkTheme = false) {
                AddNoteScreen(
                    noteId = null,
                    initialContent = null,
                    onNavigateBack = {},
                    onNavigateToAI = {},
                    viewModel = addNoteViewModel
                )
            }
        }

        testScheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Ketik Judul
        composeTestRule.onNodeWithText("Judul").performTextInput("Catatan Baru Saya")
        // Ketik Konten
        composeTestRule.onNodeWithText("Konten").performTextInput("Ini adalah isi catatan baru yang indah.")

        composeTestRule.waitForIdle()

        // Tekan tombol Simpan (IconButton dengan contentDescription = "Simpan")
        composeTestRule.onNodeWithContentDescription("Simpan").performClick()
        testScheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Verifikasi bahwa catatan telah tersimpan di repository (gunakan first() non-blocking)
        val notes = noteRepository.getAllNotes().first()
        assertEquals(1, notes.size)
        assertEquals("Catatan Baru Saya", notes.first().title)

        addNoteViewModel.viewModelScope.coroutineContext.cancelChildren()
    }
}
