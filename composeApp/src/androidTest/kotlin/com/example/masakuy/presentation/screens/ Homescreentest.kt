package com.example.masakuy.presentation.screens.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.masakuy.domain.model.Recipe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeViewModel: HomeViewModel
    private val fakeUiState = MutableStateFlow(HomeUiState())

    @Before
    fun setUp() {
        fakeViewModel = mockk(relaxed = true)
        every { fakeViewModel.uiState } returns fakeUiState
    }

    @Test
    fun homeScreen_tombolCariRekomendasi_tampil() {
        composeTestRule.setContent {
            HomeScreen(
                viewModel = fakeViewModel,
                onRecommendationClick = {},
                onRecipeClick = {},
                onFavoriteClick = {},
                onSearchClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Cari Rekomendasi 🔍")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_tombolCariRekomendasi_tanpaBudget_tidakMemanggil_callback() {
        var clicked = false
        composeTestRule.setContent {
            HomeScreen(
                viewModel = fakeViewModel,
                onRecommendationClick = { clicked = true },
                onRecipeClick = {},
                onFavoriteClick = {},
                onSearchClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Cari Rekomendasi 🔍")
            .performClick()

        composeTestRule
            .onNodeWithText("Pilih atau masukkan budget dulu")
            .assertIsDisplayed()

        assert(!clicked)
    }

    @Test
    fun homeScreen_tombolCariRekomendasi_setelahPilihBudget_memanggil_callback() {
        var budgetPassed = 0
        composeTestRule.setContent {
            HomeScreen(
                viewModel = fakeViewModel,
                onRecommendationClick = { budgetPassed = it },
                onRecipeClick = {},
                onFavoriteClick = {},
                onSearchClick = {}
            )
        }

        composeTestRule.onNodeWithText("Rp15.000").performClick()
        composeTestRule.onNodeWithText("Cari Rekomendasi 🔍").performClick()

        assert(budgetPassed == 15000)
    }

    @Test
    fun homeScreen_searchBar_tampil_dan_bisa_diklik() {
        var searchClicked = false
        composeTestRule.setContent {
            HomeScreen(
                viewModel = fakeViewModel,
                onRecommendationClick = {},
                onRecipeClick = {},
                onFavoriteClick = {},
                onSearchClick = { searchClicked = true }
            )
        }

        composeTestRule
            .onNodeWithText("Cari menu, bahan, atau budget...")
            .assertIsDisplayed()
            .performClick()

        assert(searchClicked)
    }

    @Test
    fun homeScreen_accordionFavorit_emptyState_tampil_saat_dibuka() {
        fakeUiState.value = HomeUiState(favorites = emptyList())

        composeTestRule.setContent {
            HomeScreen(
                viewModel = fakeViewModel,
                onRecommendationClick = {},
                onRecipeClick = {},
                onFavoriteClick = {},
                onSearchClick = {}
            )
        }

        composeTestRule.onNodeWithText("Menu Hemat Favorit").performClick()

        composeTestRule
            .onNodeWithText("Belum ada favorit. Simpan resep dari hasil rekomendasi!")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_accordionFavorit_menampilkan_resep_jika_ada() {
        val recipes = listOf(
            Recipe("r1", "Nasi Goreng", "", 15000, "20 menit", "Mudah", isFavorite = true),
            Recipe("r2", "Soto Ayam",   "", 20000, "30 menit", "Sedang", isFavorite = true),
        )
        fakeUiState.value = HomeUiState(favorites = recipes)

        composeTestRule.setContent {
            HomeScreen(
                viewModel = fakeViewModel,
                onRecommendationClick = {},
                onRecipeClick = {},
                onFavoriteClick = {},
                onSearchClick = {}
            )
        }

        composeTestRule.onNodeWithText("Menu Hemat Favorit").performClick()

        composeTestRule.onNodeWithText("Nasi Goreng").assertIsDisplayed()
        composeTestRule.onNodeWithText("Soto Ayam").assertIsDisplayed()
    }

    @Test
    fun homeScreen_teksGreeting_tampil() {
        composeTestRule.setContent {
            HomeScreen(
                viewModel = fakeViewModel,
                onRecommendationClick = {},
                onRecipeClick = {},
                onFavoriteClick = {},
                onSearchClick = {}
            )
        }

        composeTestRule.onNodeWithText("Hai, mau makan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masakuy").assertIsDisplayed()
    }
}