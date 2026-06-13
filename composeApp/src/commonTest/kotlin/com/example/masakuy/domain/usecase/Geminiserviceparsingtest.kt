package com.example.masakuy.domain.usecase

import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.presentation.screens.api.GeminiService
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class GeminiServiceParsingTest {

    private val httpClient = mockk<io.ktor.client.HttpClient>()
    private val service = GeminiService(httpClient)

    private fun callParseToRecipes(text: String, budget: Int): List<Recipe> {
        val method = GeminiService::class.java.getDeclaredMethod(
            "parseToRecipes", String::class.java, Int::class.java
        )
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(service, text, budget) as List<Recipe>
    }

    private fun callParseToRecipeDetail(text: String, name: String, budget: Int): RecipeDetail {
        val method = GeminiService::class.java.getDeclaredMethod(
            "parseToRecipeDetail", String::class.java, String::class.java, Int::class.java
        )
        method.isAccessible = true
        return method.invoke(service, text, name, budget) as RecipeDetail
    }

    private fun callBuildRecommendationPrompt(budget: Int): String {
        val method = GeminiService::class.java.getDeclaredMethod("buildRecommendationPrompt", Int::class.java)
        method.isAccessible = true
        return method.invoke(service, budget) as String
    }

    private fun callBuildDetailPrompt(name: String, budget: Int): String {
        val method = GeminiService::class.java.getDeclaredMethod("buildDetailPrompt", String::class.java, Int::class.java)
        method.isAccessible = true
        return method.invoke(service, name, budget) as String
    }

    @Test
    fun `parseToRecipes mem-parsing format standar dengan benar`() {
        val text = """
            1. Nasi Goreng - Rp10000
            2. Mie Goreng - Rp12000
            3. Soto Ayam - Rp8000
            4. Tempe Goreng - Rp9000
            5. Sayur Sop - Rp7000
        """.trimIndent()

        val result = callParseToRecipes(text, 15000)

        assertEquals(5, result.size)
        assertEquals("Nasi Goreng", result[0].name)
        assertEquals(10000, result[0].estimatedCost)
        assertEquals(30, result[0].estimatedTime)
        assertEquals("Mudah", result[0].difficulty)
        assertFalse(result[0].isFavorite)
    }

    @Test
    fun `parseToRecipes dengan format markdown bold`() {
        val text = "1. **Nasi Goreng** - Rp10000"

        val result = callParseToRecipes(text, 15000)

        assertEquals(1, result.size)
        assertEquals("Nasi Goreng", result[0].name)
    }

    @Test
    fun `parseToRecipes cost dibatasi maksimal budget`() {
        val text = "1. Nasi Mahal - Rp50000"

        val result = callParseToRecipes(text, 15000)

        assertEquals(15000, result[0].estimatedCost)
    }

    @Test
    fun `parseToRecipes baris kosong diabaikan`() {
        val text = """
            1. Nasi Goreng - Rp10000

            2. Mie Goreng - Rp12000
        """.trimIndent()

        val result = callParseToRecipes(text, 15000)

        assertEquals(2, result.size)
    }

    @Test
    fun `parseToRecipes baris tanpa angka di awal diabaikan`() {
        val text = """
            Berikut rekomendasi:
            1. Nasi Goreng - Rp10000
        """.trimIndent()

        val result = callParseToRecipes(text, 15000)

        assertEquals(1, result.size)
        assertEquals("Nasi Goreng", result[0].name)
    }

    @Test
    fun `parseToRecipes tanpa harga pakai budget sebagai default`() {
        val text = "1. Nasi Goreng"

        val result = callParseToRecipes(text, 15000)

        assertEquals(1, result.size)
        assertEquals(15000, result[0].estimatedCost)
    }

    @Test
    fun `parseToRecipeDetail mem-parsing format lengkap dengan benar`() {
        val text = """
            WAKTU: 25
            KESULITAN: Sedang
            BAHAN:
            - 200g beras | Rp3000
            - 2 butir telur | Rp4000
            CARA:
            1. Langkah pertama
            2. Langkah kedua
        """.trimIndent()

        val result = callParseToRecipeDetail(text, "Nasi Goreng", 15000)

        assertEquals("Nasi Goreng", result.name)
        assertEquals(25, result.estimatedTime)
        assertEquals("Sedang", result.difficulty)
        assertEquals(2, result.ingredients.size)
        assertEquals("200g beras", result.ingredients[0].name)
        assertEquals(3000, result.ingredients[0].estimatedPrice)
        assertEquals(2, result.instructions.size)
        assertEquals("Langkah pertama", result.instructions[0])
        assertFalse(result.isFavorite)
    }

    @Test
    fun `parseToRecipeDetail tanpa WAKTU dan KESULITAN pakai default`() {
        val text = """
            BAHAN:
            - Garam | Rp500
            CARA:
            1. Aduk semua
        """.trimIndent()

        val result = callParseToRecipeDetail(text, "Resep X", 10000)

        assertEquals(30, result.estimatedTime)
        assertEquals("Mudah", result.difficulty)
        assertEquals(1, result.ingredients.size)
    }

    @Test
    fun `parseToRecipeDetail bahan tanpa harga pakai 0`() {
        val text = """
            BAHAN:
            - Garam secukupnya
            CARA:
            1. Aduk
        """.trimIndent()

        val result = callParseToRecipeDetail(text, "Resep Y", 10000)

        assertEquals(0, result.ingredients[0].estimatedPrice)
    }

    @Test
    fun `parseToRecipeDetail text kosong menghasilkan default`() {
        val result = callParseToRecipeDetail("", "Resep Kosong", 10000)

        assertEquals("Resep Kosong", result.name)
        assertEquals(30, result.estimatedTime)
        assertEquals("Mudah", result.difficulty)
        assertTrue(result.ingredients.isEmpty())
        assertTrue(result.instructions.isEmpty())
        assertEquals(10000, result.estimatedCost)
    }

    @Test
    fun `buildRecommendationPrompt mengandung budget`() {
        val prompt = callBuildRecommendationPrompt(20000)

        assertTrue(prompt.contains("20000"))
        assertTrue(prompt.contains("rekomendasi"))
    }

    @Test
    fun `buildDetailPrompt mengandung nama resep dan budget`() {
        val prompt = callBuildDetailPrompt("Nasi Goreng", 15000)

        assertTrue(prompt.contains("Nasi Goreng"))
        assertTrue(prompt.contains("15000"))
        assertTrue(prompt.contains("WAKTU"))
    }
}