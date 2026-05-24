package com.example.mapenumkm.presentation.screens.product

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.model.NoteColor
import com.example.mapenumkm.presentation.theme.MaPenTheme
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test

class ProductListUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEmptyStateVisible() {
        composeTestRule.setContent {
            MaPenTheme {
                ProductListScreen(
                    state = ProductListState(products = emptyList()),
                    onBackClick = {},
                    onAddProductClick = {},
                    onEditProductClick = {},
                    onDeleteProductClick = {},
                    onSearchQueryChange = {},
                    onCategoryChange = {}
                )
            }
        }

        // Memastikan pesan "Belum ada produk" muncul saat list kosong
        composeTestRule.onNodeWithText("Belum ada produk").assertIsDisplayed()
    }

    @Test
    fun testProductListAndSearchDisplay() {
        val testProducts = listOf(
            createTestNote(1, "Sate Ayam", NoteCategory.FOOD),
            createTestNote(2, "Es Jeruk", NoteCategory.DRINK)
        )

        composeTestRule.setContent {
            MaPenTheme {
                ProductListScreen(
                    state = ProductListState(products = testProducts, searchQuery = "Sate"),
                    onBackClick = {},
                    onAddProductClick = {},
                    onEditProductClick = {},
                    onDeleteProductClick = {},
                    onSearchQueryChange = {},
                    onCategoryChange = {}
                )
            }
        }

        // Memastikan produk yang sesuai query muncul
        composeTestRule.onNodeWithText("Sate Ayam").assertIsDisplayed()
        // Dalam mode testing stateless, kita hanya mengecek apa yang dirender berdasarkan state yang diberikan
    }

    @Test
    fun testCategoryFilterInteraction() {
        var selectedCategory: NoteCategory? = null
        
        composeTestRule.setContent {
            MaPenTheme {
                ProductListScreen(
                    state = ProductListState(products = emptyList()),
                    onBackClick = {},
                    onAddProductClick = {},
                    onEditProductClick = {},
                    onDeleteProductClick = {},
                    onSearchQueryChange = {},
                    onCategoryChange = { selectedCategory = it }
                )
            }
        }

        // Klik pada filter "FOOD" (Makanan)
        // Catatan: Gunakan displayName dari NoteCategory
        composeTestRule.onNodeWithText(NoteCategory.FOOD.displayName).performClick()
        
        // Verifikasi callback terpanggil dengan kategori yang benar
        assert(selectedCategory == NoteCategory.FOOD)
    }

    private fun createTestNote(id: Long, title: String, category: NoteCategory): Note {
        return Note(
            id = id,
            title = title,
            content = "Deskripsi $title",
            price = 10000.0,
            stock = 10,
            category = category,
            color = NoteColor.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
