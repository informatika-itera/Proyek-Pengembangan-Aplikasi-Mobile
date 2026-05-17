package com.example.noteai.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.data.local.RecipeEntity
import com.example.noteai.domain.model.Recipe
import com.example.noteai.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class RecipeRepositoryImpl(
    db: NoteDatabase
) : RecipeRepository {
    private val queries = db.noteQueries
    private val ioDispatcher = Dispatchers.IO

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return queries.getAllRecipes()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return queries.getFavoriteRecipes()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getRecipeById(id: Long): Flow<Recipe?> {
        return queries.getRecipeById(id)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { it?.toDomain() }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        queries.insertRecipe(
            title = recipe.title,
            ingredients = recipe.ingredients,
            instructions = recipe.instructions,
            is_favorite = if (recipe.isFavorite) 1L else 0L,
            is_ai_generated = if (recipe.isAiGenerated) 1L else 0L,
            created_at = recipe.createdAt.toEpochMilliseconds(),
            updated_at = recipe.updatedAt.toEpochMilliseconds()
        )
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        queries.updateRecipe(
            title = recipe.title,
            ingredients = recipe.ingredients,
            instructions = recipe.instructions,
            is_favorite = if (recipe.isFavorite) 1L else 0L,
            updated_at = recipe.updatedAt.toEpochMilliseconds(),
            id = recipe.id
        )
    }

    override suspend fun deleteRecipe(id: Long) {
        queries.deleteRecipe(id)
    }

    override suspend fun toggleFavorite(id: Long) {
        queries.toggleFavorite(id)
    }

    private fun RecipeEntity.toDomain(): Recipe = Recipe(
        id = id,
        title = title,
        ingredients = ingredients,
        instructions = instructions,
        isFavorite = is_favorite == 1L,
        isAiGenerated = is_ai_generated == 1L,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}
