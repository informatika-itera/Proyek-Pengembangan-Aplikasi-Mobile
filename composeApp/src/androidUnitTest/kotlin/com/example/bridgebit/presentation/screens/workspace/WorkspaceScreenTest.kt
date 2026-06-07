package com.example.bridgebit.presentation.screens.workspace

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.repository.TranslationRepository
import com.example.bridgebit.domain.usecase.SaveTranslationUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], instrumentedPackages = ["androidx.loader.content"])
class WorkspaceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var saveTranslationUseCase: SaveTranslationUseCase
    private lateinit var repository: TranslationRepository
    private lateinit var aiRepository: AIRepository
    private lateinit var viewModel: WorkspaceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Pastikan Koin bersih sebelum test berikutnya
        try { stopKoin() } catch (_: Exception) {}

        saveTranslationUseCase = mockk(relaxed = true)
        repository = mockk()
        aiRepository = mockk()

        viewModel = WorkspaceViewModel(saveTranslationUseCase, repository, aiRepository)

        startKoin {
            modules(module {
                factory { viewModel }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    // ─── Test 1: TopAppBar title "Workspace Terjemahan" ditampilkan ──────────
    @Test
    fun workspaceScreen_displaysTopAppBarTitleForNewTranslation() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Workspace Terjemahan").assertIsDisplayed()
    }

    // ─── Test 2: TopAppBar title "Edit Terjemahan" saat mode edit ────────────
    @Test
    fun workspaceScreen_displaysEditTitleWhenTranslationIdProvided() {
        every { repository.getTranslationById(5L) } returns flowOf(
            Translation(
                id = 5L, sourceText = "Hello", translatedText = "Halo",
                sourceLanguage = "Inggris", targetLanguage = "Indonesia",
                category = "Umum", isVaulted = false,
                createdAt = 1000L, updatedAt = 1000L
            )
        )
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = 5L,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Edit Terjemahan").assertIsDisplayed()
    }

    // ─── Test 3: Tombol navigasi back ada di AppBar ───────────────────────────
    @Test
    fun workspaceScreen_backButton_isDisplayed() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Kembali").assertIsDisplayed()
    }

    // ─── Test 4: Tombol back men-trigger navigasi ─────────────────────────────
    @Test
    fun workspaceScreen_backButton_onClick_triggersNavigation() {
        var navigatedBack = false
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = { navigatedBack = true }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Kembali").performClick()
        composeTestRule.waitForIdle()
        assert(navigatedBack) { "Navigasi back seharusnya terpanggil" }
    }

    // ─── Test 5: Bahasa sumber default "Indonesia" ditampilkan ───────────────
    @Test
    fun workspaceScreen_defaultSourceLanguage_isDisplayed() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onAllNodesWithText("Indonesia").onFirst().assertIsDisplayed()
    }

    // ─── Test 6: Bahasa target default "Inggris" ditampilkan ─────────────────
    @Test
    fun workspaceScreen_defaultTargetLanguage_isDisplayed() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Inggris").assertIsDisplayed()
    }

    // ─── Test 7: Arrow symbol (➔) antar bahasa ditampilkan ───────────────────
    @Test
    fun workspaceScreen_arrowSymbol_isDisplayed() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("➔").assertIsDisplayed()
    }

    // ─── Test 8: Label input "Ketik teks asli di sini..." ditampilkan ────────
    @Test
    fun workspaceScreen_sourceTextInputLabel_isDisplayed() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Ketik teks asli di sini...").assertIsDisplayed()
    }

    // ─── Test 9: Tombol "Terjemahkan" ditampilkan saat source text kosong ─────
    @Test
    fun workspaceScreen_translateButton_isDisplayedWhenEmpty() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Terjemahkan").assertIsDisplayed()
    }

    // ─── Test 10: Tombol "Terjemahkan" disabled saat source text kosong ───────
    @Test
    fun workspaceScreen_translateButton_disabledWhenSourceTextEmpty() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Terjemahkan").assertIsNotEnabled()
    }

    // ─── Test 11: Tombol "Terjemahkan" enabled setelah ada input ─────────────
    @Test
    fun workspaceScreen_translateButton_enabledAfterInput() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        viewModel.sourceText.value = "Hello World"
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Terjemahkan").assertIsEnabled()
    }

    // ─── Test 12: Source language dropdown membuka pilihan bahasa ─────────────
    @Test
    fun workspaceScreen_sourceLanguageDropdown_opensOnClick() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        // Klik pada bahasa sumber (Indonesia)
        composeTestRule.onAllNodesWithText("Indonesia").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Jepang").assertIsDisplayed()
    }

    // ─── Test 13: Target language dropdown membuka pilihan bahasa ─────────────
    @Test
    fun workspaceScreen_targetLanguageDropdown_opensOnClick() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Inggris").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Korea").assertIsDisplayed()
    }

    // ─── Test 14: Pilih bahasa sumber dari dropdown ───────────────────────────
    @Test
    fun workspaceScreen_selectSourceLanguage_updatesLabel() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onAllNodesWithText("Indonesia").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Jepang").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Jepang").assertIsDisplayed()
    }

    // ─── Test 15: Pilih bahasa target dari dropdown ───────────────────────────
    @Test
    fun workspaceScreen_selectTargetLanguage_updatesLabel() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Inggris").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Arab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Arab").assertIsDisplayed()
    }

    // ─── Test 16: Source text input menerima ketikan ──────────────────────────
    @Test
    fun workspaceScreen_sourceTextInput_acceptsText() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Machine Learning")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Machine Learning").assertIsDisplayed()
    }

    // ─── Test 17: Error message ditampilkan saat ada error ───────────────────
    @Test
    fun workspaceScreen_errorMessage_isDisplayedWhenError() {
        viewModel.errorMessage.value = "Gagal memanggil AI: Network error"
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Gagal memanggil AI: Network error").assertIsDisplayed()
    }

    // ─── Test 18: Card output translated text ditampilkan ─────────────────────
    @Test
    fun workspaceScreen_translatedText_displayedInOutputCard() {
        viewModel.translatedText.value = "Halo Dunia"
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Halo Dunia").assertIsDisplayed()
    }

    // ─── Test 19: Loading state menampilkan "AI sedang memproses..." ──────────
    @Test
    fun workspaceScreen_loadingState_showsProcessingText() {
        viewModel.isLoading.value = true
        viewModel.sourceText.value = "Hello"
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("AI sedang memproses...").assertIsDisplayed()
    }

    // ─── Test 20: Loading state membuat tombol terjemahkan disabled ───────────
    @Test
    fun workspaceScreen_loadingState_translateButtonIsDisabled() {
        viewModel.isLoading.value = true
        viewModel.sourceText.value = "Hello"
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("AI sedang memproses...").assertIsDisplayed()
    }

    // ─── Test 21: Dropdown bahasa sumber berisi opsi "Korea" ─────────────────
    @Test
    fun workspaceScreen_sourceLanguageDropdown_containsKorea() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onAllNodesWithText("Indonesia").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Korea").assertIsDisplayed()
    }

    // ─── Test 22: Dropdown bahasa sumber berisi opsi "Jerman" ────────────────
    @Test
    fun workspaceScreen_sourceLanguageDropdown_containsJerman() {
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.onAllNodesWithText("Indonesia").onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Jerman").assertIsDisplayed()
    }

    // ─── Test 23: Error message tidak tampil saat null ───────────────────────
    @Test
    fun workspaceScreen_noErrorMessage_whenErrorIsNull() {
        viewModel.errorMessage.value = null
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Gagal memanggil AI").assertDoesNotExist()
    }

    // ─── Test 24: Klik terjemahkan men-trigger proses AI (dengan mock) ────────
    @Test
    fun workspaceScreen_translateButton_onClick_withValidInput() {
        coEvery { aiRepository.chat(any()) } returns Result.success("T: Halo\nK: Umum")
        coEvery { saveTranslationUseCase(any()) } returns Result.success(1L)

        viewModel.sourceText.value = "Hello"
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = null,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Terjemahkan").performClick()
        composeTestRule.waitForIdle()
    }

    // ─── Test 25: Mode edit memuat data dari repository ───────────────────────
    @Test
    fun workspaceScreen_editMode_loadsTranslationData() {
        every { repository.getTranslationById(10L) } returns flowOf(
            Translation(
                id = 10L, sourceText = "Deep Learning", translatedText = "Pembelajaran Mendalam",
                sourceLanguage = "Inggris", targetLanguage = "Indonesia",
                category = "Teknologi & IT", isVaulted = false,
                createdAt = 1000L, updatedAt = 1000L
            )
        )
        composeTestRule.setContent {
            KoinContext {
                WorkspaceScreen(
                    translationId = 10L,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Deep Learning").assertIsDisplayed()
    }
}