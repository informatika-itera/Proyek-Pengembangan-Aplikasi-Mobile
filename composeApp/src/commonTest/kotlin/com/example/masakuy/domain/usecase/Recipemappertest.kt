package com.example.masakuy.data.mapper

import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.RecipeDetail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecipeMapperTest {

    private val mapper = RecipeMapper()

    private fun sampleDetail(
        id: String = "1",
        name: String = "Nasi Goreng",
        isFavorite: Boolean = false
    ) = RecipeDetail(
        id = id,
        name = name,
        image = "nasi_goreng.jpg",
        estimatedCost = 15000,
        estimatedTime = 20,
        difficulty = "Mudah",
        ingredients = listOf(
            Ingredient(name = "Nasi", quantity = "1 piring", estimatedPrice = 5000),
            Ingredient(name = "Telur", quantity = "1 butir", estimatedPrice = 2000)
        ),
        instructions = listOf("Tumis bawang", "Masukkan nasi", "Goreng telur"),
        isFavorite = isFavorite
    )

    @Test
    fun `mapRecipeDetailToRecipe converts fields correctly`() {
        val detail = sampleDetail()

        val recipe = mapper.mapRecipeDetailToRecipe(detail)

        assertEquals(detail.id, recipe.id)
        assertEquals(detail.name, recipe.name)
        assertEquals(detail.image, recipe.image)
        assertEquals(detail.estimatedCost, recipe.estimatedCost)
        assertEquals(detail.estimatedTime, recipe.estimatedTime)
        assertEquals(detail.difficulty, recipe.difficulty)
    }

    @Test
    fun `mapRecipeDetailToRecipe preserves isFavorite true`() {
        val detail = sampleDetail(isFavorite = true)

        val recipe = mapper.mapRecipeDetailToRecipe(detail)

        assertTrue(recipe.isFavorite)
    }

    @Test
    fun `mapRecipeDetailToRecipe preserves isFavorite false`() {
        val detail = sampleDetail(isFavorite = false)

        val recipe = mapper.mapRecipeDetailToRecipe(detail)

        assertEquals(false, recipe.isFavorite)
    }

    @Test
    fun `parseIngredients parses valid JSON correctly`() {
        val json = """[{"name":"Nasi","quantity":"1 piring","estimatedPrice":5000},{"name":"Telur","quantity":"1 butir","estimatedPrice":2000}]"""

        val result = mapper.parseIngredients(json)

        assertEquals(2, result.size)
        assertEquals("Nasi", result[0].name)
        assertEquals("1 piring", result[0].quantity)
        assertEquals(5000, result[0].estimatedPrice)
        assertEquals("Telur", result[1].name)
    }

    @Test
    fun `parseIngredients returns empty list for invalid JSON`() {
        val invalidJson = "not a valid json"

        val result = mapper.parseIngredients(invalidJson)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseIngredients returns empty list for empty string`() {
        val result = mapper.parseIngredients("")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseIngredients handles empty JSON array`() {
        val result = mapper.parseIngredients("[]")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseInstructions parses valid JSON array of strings`() {
        val json = """["Tumis bawang","Masukkan nasi","Goreng telur"]"""

        val result = mapper.parseInstructions(json)

        assertEquals(3, result.size)
        assertEquals("Tumis bawang", result[0])
        assertEquals("Masukkan nasi", result[1])
        assertEquals("Goreng telur", result[2])
    }

    @Test
    fun `parseInstructions returns empty list for invalid JSON`() {
        val result = mapper.parseInstructions("not valid json")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseInstructions returns empty list for empty string`() {
        val result = mapper.parseInstructions("")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `serializeIngredients produces JSON that can be parsed back correctly`() {
        val ingredients = listOf(
            Ingredient(name = "Nasi", quantity = "1 piring", estimatedPrice = 5000),
            Ingredient(name = "Telur", quantity = "1 butir", estimatedPrice = 2000)
        )

        val json = mapper.serializeIngredients(ingredients)
        val parsedBack = mapper.parseIngredients(json)

        assertEquals(ingredients, parsedBack)
    }

    @Test
    fun `serializeIngredients on empty list produces empty array json`() {
        val json = mapper.serializeIngredients(emptyList())

        assertEquals("[]", json)
    }

    @Test
    fun `serializeInstructions produces JSON that can be parsed back correctly`() {
        val instructions = listOf("Langkah 1", "Langkah 2", "Langkah 3")

        val json = mapper.serializeInstructions(instructions)
        val parsedBack = mapper.parseInstructions(json)

        assertEquals(instructions, parsedBack)
    }

    @Test
    fun `serializeInstructions on empty list produces empty array json`() {
        val json = mapper.serializeInstructions(emptyList())

        assertEquals("[]", json)
    }
}