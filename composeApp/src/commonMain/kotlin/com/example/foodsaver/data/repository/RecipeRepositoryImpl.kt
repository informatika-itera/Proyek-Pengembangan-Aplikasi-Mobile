package com.example.foodsaver.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.remote.api.MealApiService
import com.example.foodsaver.data.remote.api.IndonesianRecipeApiService
import com.example.foodsaver.data.remote.api.SystemPrompts
import com.example.foodsaver.data.mapper.*
import com.example.foodsaver.domain.engine.RuleBasedRecipeEngine
import com.example.foodsaver.domain.model.*
import com.example.foodsaver.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock

class RecipeRepositoryImpl(
    private val apiService: MealApiService,
    private val indonesianApiService: IndonesianRecipeApiService,
    private val geminiService: GeminiService,
    db: FoodSaverDatabase
) : RecipeRepository {

    private val queries = db.recipeQueries
    private val ruleBasedEngine = RuleBasedRecipeEngine()

    override suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe> {
        if (ingredients.isEmpty()) return emptyList()
        
        return withContext(Dispatchers.IO) {
            try {
                val mainIngredient = ingredients.first()
                val response = apiService.searchByIngredient(mainIngredient)
                response.meals?.map { it.toDomain() } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getRecipeDetails(id: String): Recipe? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecipeDetails(id)
                val dto = response.meals?.firstOrNull() ?: return@withContext null
                val isFav = queries.getFavoriteById(id).executeAsOneOrNull() != null
                dto.toDomain(isFavorite = isFav)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun searchRecipesByName(query: String): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchByName(query)
                response.meals?.map { it.toDomain() } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getRecommendations(
        ingredients: List<RecipeIngredient>,
        preference: String,
        prioritizeExpiring: Boolean
    ): Result<RecipeRecommendation> = withContext(Dispatchers.IO) {
        if (ingredients.isEmpty()) {
            return@withContext Result.failure(Exception("Pilih minimal satu bahan terlebih dahulu."))
        }

        // 1. Indonesian API Fallback Chain
        val searchCandidates = ingredients.take(3)
        for (ingredient in searchCandidates) {
            val query = ingredient.name.lowercase()
            try {
                val searchResult = withTimeoutOrNull(5000) { 
                    indonesianApiService.searchByIngredient(query) 
                }
                if (searchResult != null && searchResult.status && searchResult.results.isNotEmpty()) {
                    for (candidate in searchResult.results.take(2)) {
                        val detailResponse = withTimeoutOrNull(4000) {
                            indonesianApiService.getRecipeDetails(candidate.key)
                        }
                        if (detailResponse != null && detailResponse.status) {
                            return@withContext Result.success(detailResponse.results.toRecommendation(ingredients))
                        }
                    }
                }
            } catch (e: Exception) {
                // Silently continue to next source
            }
        }

        // 2. Global API (TheMealDB)
        try {
            val englishIngredients = ingredients.map { IngredientMapper.mapToEnglish(it.name) }.distinct()
            val mealMap = mutableMapOf<String, Int>()
            val searchTerms = englishIngredients.take(3)
            
            for (term in searchTerms) {
                val response = withTimeoutOrNull(4000) { apiService.searchByIngredient(term) }
                response?.meals?.forEach { meal ->
                    mealMap[meal.id] = (mealMap[meal.id] ?: 0) + 1
                }
            }
            
            if (mealMap.isNotEmpty()) {
                val topCandidates = mealMap.toList().sortedByDescending { it.second }.take(2)
                val recommendations = mutableListOf<RecipeRecommendation>()
                
                for ((id, _) in topCandidates) {
                    val detailResponse = withTimeoutOrNull(4000) { apiService.getRecipeDetails(id) }
                    detailResponse?.meals?.firstOrNull()?.let { dto ->
                        val recipeIngredientsList = dto.getIngredientsList()
                        val matchedCount = englishIngredients.count { userIng -> 
                            recipeIngredientsList.any { it.contains(userIng, ignoreCase = true) }
                        }
                        val matchScore = matchedCount.toDouble() / (englishIngredients.size.coerceAtLeast(1))
                        recommendations.add(dto.toRecommendation(matchScore, ingredients))
                    }
                }
                
                if (recommendations.isNotEmpty()) {
                    return@withContext Result.success(recommendations.maxBy { it.matchScore })
                }
            }
        } catch (e: Exception) {
            // Silently continue to local fallback
        }
        
        // 3. Local Rule-Based Fallback
        try {
            val localFallback = ruleBasedEngine.generateRecommendations(ingredients, preference, prioritizeExpiring)
            Result.success(localFallback)
        } catch (e: Exception) {
            Result.failure(Exception("Resep belum bisa dimuat. Periksa koneksi internet kamu lalu coba lagi."))
        }
    }

    override suspend fun generateAiRecipe(
        ingredients: List<RecipeIngredient>,
        preference: String,
        prioritizeExpiring: Boolean
    ): Result<RecipeRecommendation> {
        val prompt = buildRecipePrompt(ingredients, preference, prioritizeExpiring)
        val systemPrompt = SystemPrompts.RECIPE_SUGGESTER

        return geminiService.generateContent(prompt, systemPrompt).map { responseText ->
            parseAiResponse(responseText)
        }
    }

    private fun buildRecipePrompt(
        ingredients: List<RecipeIngredient>,
        preference: String,
        prioritizeExpiring: Boolean
    ): String {
        val ingredientList = ingredients.joinToString("\n") { 
            "- ${it.name} (${it.quantity}), Status: ${it.expiryStatus ?: "Aman"}${it.daysLeft?.let { d -> ", Sisa hari: $d" } ?: ""}"
        }
        
        return """
            Bahan yang tersedia:
            $ingredientList

            Preferensi: $preference
            Prioritaskan bahan hampir expired: ${if (prioritizeExpiring) "Ya" else "Tidak"}

            Buatkan rekomendasi resep terbaik sesuai format.
        """.trimIndent()
    }

    private fun parseAiResponse(text: String): RecipeRecommendation {
        val lines = text.lines()
        
        fun getValue(header: String): String {
            val startIndex = lines.indexOfFirst { it.startsWith(header, ignoreCase = true) }
            if (startIndex == -1) return ""
            
            val result = mutableListOf<String>()
            for (i in startIndex + 1 until lines.size) {
                val line = lines[i]
                if (line.contains(":") && !line.startsWith("-") && line.getOrNull(0)?.isDigit() != true) break
                if (line.isNotBlank()) result.add(line.trim())
            }
            return result.joinToString("\n")
        }

        fun getSimpleValue(header: String): String {
            val line = lines.find { it.startsWith(header, ignoreCase = true) }
            return line?.substringAfter(":")?.trim() ?: ""
        }

        val title = getSimpleValue("Judul Resep")
        val reason = getSimpleValue("Cocok Karena")
        val ingredientsRaw = getValue("Bahan Utama")
        val usedIngredients = ingredientsRaw.lines().map { it.trim().removePrefix("-").trim() }.filter { it.isNotEmpty() }
        
        val optionalRaw = getValue("Bahan Tambahan Opsional")
        val optionalIngredients = optionalRaw.lines().map { it.trim().removePrefix("-").trim() }.filter { it.isNotEmpty() }
        
        val timeStr = getSimpleValue("Estimasi Waktu")
        val time = timeStr.filter { it.isDigit() }.toIntOrNull() ?: 20
        
        val difficulty = getSimpleValue("Tingkat Kesulitan")
        
        val stepsRaw = getValue("Langkah Memasak")
        val steps = stepsRaw.lines().map { it.trim().replace(Regex("^\\d+\\.\\s*"), "").trim() }.filter { it.isNotEmpty() }
        
        val note = getSimpleValue("Catatan FoodSaver")

        return RecipeRecommendation(
            title = title,
            description = "Rekomendasi AI FoodSaver",
            usedIngredients = usedIngredients,
            optionalIngredients = optionalIngredients,
            cookingTimeMinutes = time,
            difficulty = difficulty,
            reason = reason,
            steps = steps,
            warningMessage = note.takeIf { it.isNotBlank() }
        )
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
        return withContext(Dispatchers.IO) {
            queries.getFavoriteById(id).executeAsOneOrNull() != null
        }
    }
}
