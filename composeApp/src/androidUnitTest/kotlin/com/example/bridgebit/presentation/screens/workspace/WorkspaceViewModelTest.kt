package com.example.bridgebit.presentation.screens.workspace

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.repository.TranslationRepository
import com.example.bridgebit.domain.usecase.SaveTranslationUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WorkspaceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var saveTranslationUseCase: SaveTranslationUseCase
    private lateinit var repository: TranslationRepository
    private lateinit var aiRepository: AIRepository
    private lateinit var viewModel: WorkspaceViewModel

    private val existingTranslation = Translation(
        id = 5L,
        sourceText = "Deep Learning",
        translatedText = "Pembelajaran Mendalam",
        sourceLanguage = "Inggris",
        targetLanguage = "Indonesia",
        category = "Teknologi & IT",
        isVaulted = false,
        createdAt = 1000L,
        updatedAt = 1000L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        saveTranslationUseCase = mockk(relaxed = true)
        repository = mockk()
        aiRepository = mockk()

        viewModel = WorkspaceViewModel(saveTranslationUseCase, repository, aiRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Test 1: Initial state values ─────────────────────────────────────────
    @Test
    fun `initial state has empty texts and default languages`() {
        assertEquals("", viewModel.sourceText.value)
        assertEquals("", viewModel.translatedText.value)
        assertEquals("Indonesia", viewModel.sourceLanguage.value)
        assertEquals("Inggris", viewModel.targetLanguage.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
    }

    // ─── Test 2: loadTranslation populates state correctly ────────────────────
    @Test
    fun `loadTranslation populates all fields from repository`() = runTest {
        every { repository.getTranslationById(5L) } returns flowOf(existingTranslation)

        viewModel.loadTranslation(5L)
        advanceUntilIdle()

        assertEquals("Deep Learning", viewModel.sourceText.value)
        assertEquals("Pembelajaran Mendalam", viewModel.translatedText.value)
        assertEquals("Inggris", viewModel.sourceLanguage.value)
        assertEquals("Indonesia", viewModel.targetLanguage.value)
        assertEquals("Teknologi & IT", viewModel.category.value)
        assertEquals(5L, viewModel.currentTranslationId)
    }

    // ─── Test 3: loadTranslation with non-existent id → no state change ──────
    @Test
    fun `loadTranslation with null result does not update fields`() = runTest {
        every { repository.getTranslationById(999L) } returns flowOf(null)

        viewModel.loadTranslation(999L)
        advanceUntilIdle()

        // Default values tetap karena translation tidak ditemukan
        assertEquals("", viewModel.sourceText.value)
        assertEquals("", viewModel.translatedText.value)
    }

    // ─── Test 4: translateText with blank source does nothing ─────────────────
    @Test
    fun `translateText with blank sourceText does not call aiRepository`() = runTest {
        viewModel.sourceText.value = "   "

        viewModel.translateText()
        advanceUntilIdle()

        coVerify(exactly = 0) { aiRepository.chat(any()) }
        assertFalse(viewModel.isLoading.value)
    }

    // ─── Test 5: translateText sets isLoading then clears it ──────────────────
    @Test
    fun `translateText sets isLoading to true then false after completion`() = runTest {
        viewModel.sourceText.value = "Hello"

        val aiResponse = "T: Halo\nK: Umum"
        coEvery { aiRepository.chat(any()) } returns Result.success(aiResponse)
        coEvery { saveTranslationUseCase(any()) } returns Result.success(1L)

        viewModel.translateText()
        advanceUntilIdle()

        // Setelah selesai, isLoading harus false
        assertFalse(viewModel.isLoading.value)
    }

    // ─── Test 6: translateText on success updates translatedText ──────────────
    @Test
    fun `translateText on AI success updates translatedText and category`() = runTest {
        viewModel.sourceText.value = "Hello"

        val aiResponse = "T: Halo\nK: Umum"
        coEvery { aiRepository.chat(any()) } returns Result.success(aiResponse)
        coEvery { saveTranslationUseCase(any()) } returns Result.success(1L)

        viewModel.translateText()
        advanceUntilIdle()

        assertEquals("Halo", viewModel.translatedText.value)
        assertEquals("Umum", viewModel.category.value)
        assertNull(viewModel.errorMessage.value)
    }

    // ─── Test 7: translateText on failure sets errorMessage ───────────────────
    @Test
    fun `translateText on AI failure sets errorMessage`() = runTest {
        viewModel.sourceText.value = "Hello"

        coEvery { aiRepository.chat(any()) } returns Result.failure(Exception("Network timeout"))

        viewModel.translateText()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.errorMessage.value!!.contains("Gagal memanggil AI"))
    }

    // ─── Test 8: saveTranslation calls SaveTranslationUseCase ────────────────
    @Test
    fun `saveTranslation calls SaveTranslationUseCase with correct data`() = runTest {
        viewModel.sourceText.value = "Algorithm"
        viewModel.translatedText.value = "Algoritma"
        viewModel.sourceLanguage.value = "Inggris"
        viewModel.targetLanguage.value = "Indonesia"
        viewModel.category.value = "Teknologi & IT"

        coEvery { saveTranslationUseCase(any()) } returns Result.success(1L)

        var callbackCalled = false
        viewModel.saveTranslation { callbackCalled = true }
        advanceUntilIdle()

        coVerify(exactly = 1) {
            saveTranslationUseCase(match { translation ->
                translation.sourceText == "Algorithm" &&
                        translation.translatedText == "Algoritma" &&
                        translation.category == "Teknologi & IT"
            })
        }
        assertTrue(callbackCalled)
    }

    // ─── Test 9: saveTranslation with blank translatedText uses fallback ──────
    @Test
    fun `saveTranslation with blank translatedText uses fallback string`() = runTest {
        viewModel.sourceText.value = "Test"
        viewModel.translatedText.value = ""
        viewModel.category.value = ""

        coEvery { saveTranslationUseCase(any()) } returns Result.success(1L)

        viewModel.saveTranslation {}
        advanceUntilIdle()

        coVerify {
            saveTranslationUseCase(match { translation ->
                translation.translatedText == "Belum ada terjemahan" &&
                        translation.category == "Umum"
            })
        }
    }

    // ─── Test 10: translateText calls aiRepository with correct prompt content ─
    @Test
    fun `translateText includes source and target language in prompt`() = runTest {
        viewModel.sourceText.value = "Hello"
        viewModel.sourceLanguage.value = "Inggris"
        viewModel.targetLanguage.value = "Jepang"

        coEvery { aiRepository.chat(any()) } returns Result.success("T: Konnichiwa\nK: Umum")
        coEvery { saveTranslationUseCase(any()) } returns Result.success(1L)

        viewModel.translateText()
        advanceUntilIdle()

        coVerify {
            aiRepository.chat(match { prompt ->
                prompt.contains("Inggris") && prompt.contains("Jepang") && prompt.contains("Hello")
            })
        }
    }

    // ─── Test 11: AI response without "T:" prefix still parsed gracefully ─────
    @Test
    fun `translateText handles AI response without T prefix gracefully`() = runTest {
        viewModel.sourceText.value = "Hello"

        // AI tidak mengikuti format → tidak ada "T:" maupun "K:"
        coEvery { aiRepository.chat(any()) } returns Result.success("Halo saja")
        coEvery { saveTranslationUseCase(any()) } returns Result.success(1L)
        viewModel.translateText()
        advanceUntilIdle()

        // translatedText diisi dengan raw response jika tidak ada prefix
        assertEquals("Halo saja", viewModel.translatedText.value)
        // category fallback ke "Umum" karena tidak ada "K:"
        assertEquals("Umum", viewModel.category.value)
    }
}