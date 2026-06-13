package com.example.masakuy.domain.usecase

import app.cash.sqldelight.Query
import com.example.masakuy.`data`.local.Recipe as DbRecipe
import com.example.masakuy.`data`.local.RecipeQueries
import com.example.masakuy.data.local.database.MasakuyDatabase
import com.example.masakuy.data.repository.RecipeSeeder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class RecipeSeederTest {

    private val database = mockk<MasakuyDatabase>()
    private val recipeQueries = mockk<RecipeQueries>(relaxed = true)

    init {
        every { database.recipeQueries } returns recipeQueries
    }

    @Test
    fun `seedIfEmpty tidak insert jika data sudah ada`() {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsList() } returns listOf(
            DbRecipe("r1", "Nasi", "", 1000L, 10L, "Mudah", "[]", "[]", 0L)
        )
        every { recipeQueries.getAllRecipes() } returns query

        val seeder = RecipeSeeder(database)
        seeder.seedIfEmpty()

        verify(exactly = 0) { recipeQueries.insertRecipe(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `seedIfEmpty insert semua resep jika database kosong`() {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsList() } returns emptyList()
        every { recipeQueries.getAllRecipes() } returns query

        val seeder = RecipeSeeder(database)
        seeder.seedIfEmpty()

        verify(exactly = 8) { recipeQueries.insertRecipe(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }
}