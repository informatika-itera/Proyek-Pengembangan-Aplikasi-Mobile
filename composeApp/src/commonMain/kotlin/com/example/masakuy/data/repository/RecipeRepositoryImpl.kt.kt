package com.example.masakuy.data.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.data.local.database.MasakuyDatabase
import com.example.masakuy.data.mapper.RecipeMapper
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecipeRepositoryImpl(
    private val database: MasakuyDatabase,
    private val mapper: RecipeMapper
) : RecipeRepository {

    override fun getAllRecipes(): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.getAllRecipes().executeAsList()
            val mappedRecipes = recipes.map {
                Recipe(
                    id = it.id,
                    name = it.name,
                    image = it.image,
                    estimatedCost = it.estimatedCost.toInt(),
                    estimatedTime = it.estimatedTime.toInt(),
                    difficulty = it.difficulty,
                    isFavorite = it.isFavorite == 1L
                )
            }
            emit(Result.Success(mappedRecipes))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getRecipeById(id: String): Flow<Result<RecipeDetail>> = flow {
        try {
            emit(Result.Loading)
            val recipe = database.recipeQueries.getRecipeById(id).executeAsOneOrNull()
            if (recipe != null) {
                val detail = RecipeDetail(
                    id = recipe.id,
                    name = recipe.name,
                    image = recipe.image,
                    estimatedCost = recipe.estimatedCost.toInt(),
                    estimatedTime = recipe.estimatedTime.toInt(),
                    difficulty = recipe.difficulty,
                    ingredients = mapper.parseIngredients(recipe.ingredients),
                    instructions = mapper.parseInstructions(recipe.instructions),
                    isFavorite = recipe.isFavorite == 1L
                )
                emit(Result.Success(detail))
            } else {
                emit(Result.Error(Exception("Recipe not found")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun searchRecipes(query: String): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.searchRecipes(query).executeAsList()
            val mappedRecipes = recipes.map {
                Recipe(
                    id = it.id,
                    name = it.name,
                    image = it.image,
                    estimatedCost = it.estimatedCost.toInt(),
                    estimatedTime = it.estimatedTime.toInt(),
                    difficulty = it.difficulty,
                    isFavorite = it.isFavorite == 1L
                )
            }
            emit(Result.Success(mappedRecipes))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getRecipesByBudget(budget: Int): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.getRecipesByBudget(budget.toLong()).executeAsList()
            val mappedRecipes = recipes.map {
                Recipe(
                    id = it.id,
                    name = it.name,
                    image = it.image,
                    estimatedCost = it.estimatedCost.toInt(),
                    estimatedTime = it.estimatedTime.toInt(),
                    difficulty = it.difficulty,
                    isFavorite = it.isFavorite == 1L
                )
            }
            emit(Result.Success(mappedRecipes))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun saveFavorite(recipeId: String, isFavorite: Boolean) {
        try {
            database.recipeQueries.updateFavorite(if (isFavorite) 1L else 0L, recipeId)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getFavoriteRecipes(): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.getFavorites().executeAsList()
            val mappedRecipes = recipes.map {
                Recipe(
                    id = it.id,
                    name = it.name,
                    image = it.image,
                    estimatedCost = it.estimatedCost.toInt(),
                    estimatedTime = it.estimatedTime.toInt(),
                    difficulty = it.difficulty,
                    isFavorite = it.isFavorite == 1L
                )
            }
            emit(Result.Success(mappedRecipes))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}