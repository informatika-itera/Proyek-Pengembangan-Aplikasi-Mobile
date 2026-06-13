package com.example.masakuy.presentation.screens.detail

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.RecipeDetail
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: DetailViewModel
    private lateinit var fakeUiState: MutableStateFlow<DetailUiState>

    private val fakeRecipe = RecipeDetail(
        id = "recipe-1",
        name = "Nasi Goreng",
        estimatedCost = 15000,
        estimatedTime = 20,
        difficulty = "Mudah",
        description = "Nasi goreng spesial",
        image = "",
        ingredients = listOf(
            Ingredient(name = "Nasi putih", quantity = "1 piring", estimatedPrice = 5000),
            Ingredient(name = "Telur ayam", quantity = "1 butir",  estimatedPrice = 3000),
            Ingredient(name = "Kecap manis", quantity = "1 sdm",   estimatedPrice = 2000)
        ),
        instructions = listOf(
            "Panaskan minyak di wajan",
            "Masukkan nasi dan aduk rata",
            "Tambahkan kecap dan bumbu"
        ),
        isFavorite = false
    )

    @Before
    fun setup() {
        fakeUiState = MutableStateFlow(DetailUiState())
        viewModel = mockk(relaxed = true)
        every { viewModel.uiState } returns fakeUiState
    }

    private fun setContent(onBackClick: () -> Unit = {}) {
        composeTestRule.setContent {
            DetailScreen(
                recipeId = "recipe-1",
                recipeName = "Nasi Goreng",
                budget = 15000,
                viewModel = viewModel,
                onBackClick = onBackClick
            )
        }
    }

    // ── Loading State ─────────────────────────────────────────────────────────

    @Test
    fun loadingState_progressIndicatorTampil() {
        fakeUiState.value = DetailUiState(isLoading = true)
        setContent()

        composeTestRule
            .onNode(
                hasTestTag("CircularProgressIndicator").or(isCircularProgressIndicator())
            )
            .assertExists()
    }

    @Test
    fun loadingState_kontenResepTidakTampil() {
        fakeUiState.value = DetailUiState(isLoading = true)
        setContent()

        composeTestRule.onNodeWithText("Bahan-bahan").assertDoesNotExist()
        composeTestRule.onNodeWithText("Cara Membuat").assertDoesNotExist()
    }

    // ── Error State ───────────────────────────────────────────────────────────

    @Test
    fun errorState_pesanGagalTampil() {
        fakeUiState.value = DetailUiState(error = "Gagal terhubung ke server")
        setContent()

        composeTestRule.onNodeWithText("Koneksi bermasalah").assertIsDisplayed()
    }

    @Test
    fun errorState_rateLimitMenunjukkan429Message() {
        fakeUiState.value = DetailUiState(error = "429 quota exceeded")
        setContent()

        composeTestRule.onNodeWithText("AI-nya lagi sibuk!").assertIsDisplayed()
    }

    @Test
    fun errorState_errorLain_pesanUmumTampil() {
        fakeUiState.value = DetailUiState(error = "Unknown error occurred")
        setContent()

        composeTestRule.onNodeWithText("Gagal memuat resep").assertIsDisplayed()
    }

    @Test
    fun errorState_tombolCobaLagiTampil() {
        fakeUiState.value = DetailUiState(error = "Gagal terhubung ke server")
        setContent()

        composeTestRule.onNodeWithText("Coba Lagi").assertIsDisplayed()
    }

    @Test
    fun errorState_klikCobaLagi_memanggilLoadRecipe() {
        fakeUiState.value = DetailUiState(error = "network timeout")
        setContent()

        composeTestRule.onNodeWithText("Coba Lagi").performClick()

        verify { viewModel.loadRecipe("recipe-1", "Nasi Goreng", 15000) }
    }

    // ── Content State ─────────────────────────────────────────────────────────

    @Test
    fun contentState_namaResepTampilDiTopBar() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("Nasi Goreng").assertIsDisplayed()
    }

    @Test
    fun contentState_seksiBahanTampil() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("Bahan-bahan").assertIsDisplayed()
    }

    @Test
    fun contentState_daftarBahanTampilSemua() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("Nasi putih").assertIsDisplayed()
        composeTestRule.onNodeWithText("Telur ayam").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kecap manis").assertIsDisplayed()
    }

    @Test
    fun contentState_seksiCaraMemasakTampil() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("Cara Membuat").assertIsDisplayed()
    }

    @Test
    fun contentState_langkahMemasakTampil() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("Panaskan minyak di wajan").assertIsDisplayed()
    }

    @Test
    fun contentState_estimasiBiayaTampil() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("💰 Rp15.000").assertIsDisplayed()
    }

    @Test
    fun contentState_tombolSimpanFavoritTampil() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("🤍 Simpan ke Favorit").assertIsDisplayed()
    }

    @Test
    fun contentState_klikSimpanFavorit_memanggilToggleFavorite() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        setContent()

        composeTestRule.onNodeWithText("🤍 Simpan ke Favorit").performClick()

        verify { viewModel.toggleFavorite("recipe-1", true) }
    }

    @Test
    fun contentState_resepSudahFavorit_tombolHapusTampil() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe.copy(isFavorite = true))
        setContent()

        composeTestRule.onNodeWithText("🗑️ Hapus dari Favorit").assertIsDisplayed()
    }

    @Test
    fun contentState_klikHapusFavorit_memanggilToggleFavoriteFalse() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe.copy(isFavorite = true))
        setContent()

        composeTestRule.onNodeWithText("🗑️ Hapus dari Favorit").performClick()

        verify { viewModel.toggleFavorite("recipe-1", false) }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Test
    fun backButton_klik_memanggilOnBackClick() {
        fakeUiState.value = DetailUiState(recipe = fakeRecipe)
        val onBack = mockk<() -> Unit>(relaxed = true)
        setContent(onBackClick = onBack)

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        verify { onBack() }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun isCircularProgressIndicator(): SemanticsMatcher =
        hasContentDescription("Loading").or(
            SemanticsMatcher("isProgressBar") { node ->
                node.config.getOrNull(SemanticsProperties.ProgressBarRangeInfo) != null
            }
        )
}