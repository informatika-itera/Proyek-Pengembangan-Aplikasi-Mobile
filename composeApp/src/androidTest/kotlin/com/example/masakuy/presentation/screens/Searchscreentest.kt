package com.example.masakuy.presentation.screens.search

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

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeViewModel: SearchViewModel
    private val fakeUiState = MutableStateFlow(SearchUiState())

    @Before
    fun setUp() {
        fakeViewModel = mockk(relaxed = true)
        every { fakeViewModel.uiState } returns fakeUiState
    }

    @Test
    fun searchScreen_searchField_tampil() {
        composeTestRule.setContent {
            SearchScreen(
                viewModel = fakeViewModel,
                onRecipeClick = {},
                onBackClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Cari menu, bahan, atau resep...")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_searchField_input_memanggil_updateQuery() {
        composeTestRule.setContent {
            SearchScreen(
                viewModel = fakeViewModel,
                onRecipeClick = {},
                onBackClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Cari menu, bahan, atau resep...")
            .performClick()
            .performTextInput("Nasi")

        verify { fakeViewModel.updateQuery("Nasi") }
    }

    @Test
    fun searchScreen_emptyState_tampil_jika_query_ada_tapi_hasil_kosong() {
        fakeUiState.value = SearchUiState(query = "Bakso", results = emptyList(), isLoading = false)

        composeTestRule.setContent {
            SearchScreen(viewModel = fakeViewModel, onRecipeClick = {}, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Resep tidak ditemukan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Coba kata kunci lain").assertIsDisplayed()
    }

    @Test
    fun searchScreen_emptyState_default_tampil_jika_query_kosong() {
        fakeUiState.value = SearchUiState(query = "", results = emptyList(), isLoading = false)

        composeTestRule.setContent {
            SearchScreen(viewModel = fakeViewModel, onRecipeClick = {}, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Ketik untuk mencari resep").assertIsDisplayed()
    }

    @Test
    fun searchScreen_loading_tidak_menampilkan_hasil() {
        fakeUiState.value = SearchUiState(query = "Nasi", isLoading = true, results = emptyList())

        composeTestRule.setContent {
            SearchScreen(viewModel = fakeViewModel, onRecipeClick = {}, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Resep tidak ditemukan").assertDoesNotExist()
    }

    @Test
    fun searchScreen_menampilkan_hasil_pencarian_jika_ada() {
        val recipes = listOf(
            Recipe("r1", "Nasi Goreng", "", 15000, "20 menit", "Mudah", isFavorite = false),
            Recipe("r2", "Nasi Uduk",   "", 18000, "25 menit", "Mudah", isFavorite = false),
        )
        fakeUiState.value = SearchUiState(query = "Nasi", results = recipes, isLoading = false)

        composeTestRule.setContent {
            SearchScreen(viewModel = fakeViewModel, onRecipeClick = {}, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Nasi Goreng").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nasi Uduk").assertIsDisplayed()
    }

    @Test
    fun searchScreen_klik_resep_memanggil_onRecipeClick() {
        val recipes = listOf(
            Recipe("r1", "Nasi Goreng", "", 15000, "20 menit", "Mudah", isFavorite = false),
        )
        fakeUiState.value = SearchUiState(query = "Nasi", results = recipes, isLoading = false)

        var clickedId = ""
        composeTestRule.setContent {
            SearchScreen(
                viewModel = fakeViewModel,
                onRecipeClick = { clickedId = it },
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Nasi Goreng").performClick()

        assert(clickedId == "r1")
    }

    @Test
    fun searchScreen_filter_budget_tampil() {
        composeTestRule.setContent {
            SearchScreen(viewModel = fakeViewModel, onRecipeClick = {}, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Di bawah 10k").assertIsDisplayed()
        composeTestRule.onNodeWithText("10k - 20k").assertIsDisplayed()
        composeTestRule.onNodeWithText("Di atas 20k").assertIsDisplayed()
    }

    @Test
    fun searchScreen_klik_filter_budget_memanggil_filterByBudget() {
        composeTestRule.setContent {
            SearchScreen(viewModel = fakeViewModel, onRecipeClick = {}, onBackClick = {})
        }

        composeTestRule.onNodeWithText("10k - 20k").performClick()

        verify { fakeViewModel.filterByBudget(20000) }
    }

    @Test
    fun searchScreen_judul_topBar_tampil() {
        composeTestRule.setContent {
            SearchScreen(viewModel = fakeViewModel, onRecipeClick = {}, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Cari Resep").assertIsDisplayed()
    }

    @Test
    fun searchScreen_tombol_back_memanggil_onBackClick() {
        var backClicked = false
        composeTestRule.setContent {
            SearchScreen(
                viewModel = fakeViewModel,
                onRecipeClick = {},
                onBackClick = { backClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Kembali").performClick()

        assert(backClicked)
    }
}