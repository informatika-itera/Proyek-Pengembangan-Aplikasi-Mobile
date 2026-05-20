package com.example.foodsaver.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.data.remote.api.MealApiService
import com.example.foodsaver.data.remote.dto.MealDto
import com.example.foodsaver.domain.model.IngredientAmount
import com.example.foodsaver.domain.model.Recipe
import com.example.foodsaver.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class RecipeRepositoryImpl(
    private val apiService: MealApiService,
    private val db: FoodSaverDatabase
) : RecipeRepository {

    private val queries = db.recipeQueries

    override suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe> {
        if (ingredients.isEmpty()) return emptyList()
        
        val mainIngredient = ingredients.first()
        val response = apiService.searchByIngredient(mainIngredient)
        
        return response.meals?.map { dto ->
            dto.toDomain()
        } ?: emptyList()
    }

    override suspend fun getRecipeDetails(id: String): Recipe? {
        val response = apiService.getRecipeDetails(id)
        val dto = response.meals?.firstOrNull() ?: return null
        val isFav = queries.getFavoriteById(id).executeAsOneOrNull() != null
        return dto.toDomain().copy(isFavorite = isFav)
    }

    override suspend fun searchRecipesByName(query: String): List<Recipe> {
        val response = apiService.searchByName(query)
        return response.meals?.map { it.toDomain() } ?: emptyList()
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return queries.getAllFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Recipe(
                        id = entity.id,
                        name = entity.name,
                        imageUrl = entity.imageUrl,
                        category = entity.category,
                        area = entity.area,
                        instructions = entity.instructions,
                        ingredients = emptyList(), 
                        isFavorite = true
                    )
                }
            }
    }

    override suspend fun toggleFavorite(recipe: Recipe) {
        val existing = queries.getFavoriteById(recipe.id).executeAsOneOrNull()
        if (existing != null) {
            queries.deleteFavorite(recipe.id)
        } else {
            queries.insertFavorite(
                id = recipe.id,
                name = recipe.name,
                imageUrl = recipe.imageUrl,
                category = recipe.category,
                area = recipe.area,
                instructions = recipe.instructions,
                ingredients = "", 
                dateSaved = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun isFavorite(id: String): Boolean {
        return queries.getFavoriteById(id).executeAsOneOrNull() != null
    }

    private fun MealDto.toDomain(): Recipe {
        val ingredientsList = mutableListOf<IngredientAmount>()
        
        fun addIfNotEmpty(ingredient: String?, measure: String?) {
            if (!ingredient.isNullOrBlank()) {
                ingredientsList.add(IngredientAmount(ingredient, measure ?: ""))
            }
        }

        addIfNotEmpty(strIngredient1, strMeasure1)
        addIfNotEmpty(strIngredient2, strMeasure2)
        addIfNotEmpty(strIngredient3, strMeasure3)
        addIfNotEmpty(strIngredient4, strMeasure4)
        addIfNotEmpty(strIngredient5, strMeasure5)
        addIfNotEmpty(strIngredient6, strMeasure6)
        addIfNotEmpty(strIngredient7, strMeasure7)
        addIfNotEmpty(strIngredient8, strMeasure8)
        addIfNotEmpty(strIngredient9, strMeasure9)
        addIfNotEmpty(strIngredient10, strMeasure10)
        addIfNotEmpty(strIngredient11, strMeasure11)
        addIfNotEmpty(strIngredient12, strMeasure12)
        addIfNotEmpty(strIngredient13, strMeasure13)
        addIfNotEmpty(strIngredient14, strMeasure14)
        addIfNotEmpty(strIngredient15, strMeasure15)
        addIfNotEmpty(strIngredient16, strMeasure16)
        addIfNotEmpty(strIngredient17, strMeasure17)
        addIfNotEmpty(strIngredient18, strMeasure18)
        addIfNotEmpty(strIngredient19, strMeasure19)
        addIfNotEmpty(strIngredient20, strMeasure20)

        return Recipe(
            id = id,
            name = name,
            imageUrl = thumbUrl ?: "",
            category = category,
            area = area,
            instructions = instructions,
            ingredients = ingredientsList
        )
    }
}
