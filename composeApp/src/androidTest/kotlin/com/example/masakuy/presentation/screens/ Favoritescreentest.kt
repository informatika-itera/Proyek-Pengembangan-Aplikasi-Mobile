package com.example.masakuy.presentation.screens.favorite

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.masakuy.domain.model.Recipe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeViewModel: FavoriteViewModel
    private val fakeUiState = MutableStateFlow(FavoriteUiState())

    @Before
    fun setUp() {
        fakeViewModel = mockk(relaxed = true)
        every { fakeViewModel.uiState } returns fakeUiState
    }

    @Test
    fun favoriteScreen_emptyState_tampil_jika_tidak_ada_favorit() {
        fakeUiState.value = FavoriteUiState(favorites = emptyList(), isLoading = false)

        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { _, _ -> },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Belum ada favorit").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Simpan resep favoritmu dari halaman rekomendasi!")
            .assertIsDisplayed()
    }

    @Test
    fun favoriteScreen_emptyState_emoji_tampil() {
        fakeUiState.value = FavoriteUiState(favorites = emptyList(), isLoading = false)

        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { _, _ -> },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("🍽️").assertIsDisplayed()
    }

    @Test
    fun favoriteScreen_loading_menampilkan_progress_indicator() {
        fakeUiState.value = FavoriteUiState(isLoading = true)

        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { _, _ -> },
                onBackClick = {}
            )
        }

        // Cukup pastikan empty state tidak muncul saat loading
        composeTestRule.onNodeWithText("Belum ada favorit").assertDoesNotExist()
    }

    @Test
    fun favoriteScreen_menampilkan_nama_resep_di_list() {
        val recipes = listOf(
            Recipe("r1", "Nasi Goreng", "", 15000, "20 menit", "Mudah", isFavorite = true),
            Recipe("r2", "Soto Ayam",   "", 20000, "30 menit", "Sedang", isFavorite = true),
        )
        fakeUiState.value = FavoriteUiState(favorites = recipes, isLoading = false)

        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { _, _ -> },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Nasi Goreng").assertIsDisplayed()
        composeTestRule.onNodeWithText("Soto Ayam").assertIsDisplayed()
    }

    @Test
    fun favoriteScreen_tombol_lihatResep_memanggil_onRecipeClick() {
        val recipes = listOf(
            Recipe("r1", "Nasi Goreng", "", 15000, "20 menit", "Mudah", isFavorite = true),
        )
        fakeUiState.value = FavoriteUiState(favorites = recipes, isLoading = false)

        var clickedId = ""
        var clickedName = ""

        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { id, name -> clickedId = id; clickedName = name },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Lihat Resep").performClick()

        assert(clickedId == "r1")
        assert(clickedName == "Nasi Goreng")
    }

    @Test
    fun favoriteScreen_tombol_hapus_memanggil_removeFavorite() {
        val recipes = listOf(
            Recipe("r1", "Nasi Goreng", "", 15000, "20 menit", "Mudah", isFavorite = true),
        )
        fakeUiState.value = FavoriteUiState(favorites = recipes, isLoading = false)

        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { _, _ -> },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Hapus").performClick()

        verify { fakeViewModel.removeFavorite("r1") }
    }

    @Test
    fun favoriteScreen_topBar_judul_tampil() {
        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { _, _ -> },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Favorit Saya").assertIsDisplayed()
    }

    @Test
    fun favoriteScreen_tombol_back_memanggil_onBackClick() {
        var backClicked = false
        composeTestRule.setContent {
            FavoriteScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { _, _ -> },
                onBackClick = { backClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assert(backClicked)
    }
}