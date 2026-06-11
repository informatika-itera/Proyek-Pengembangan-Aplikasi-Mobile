package com.example.foodsaver.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.remote.api.MealApiService
import com.example.foodsaver.data.remote.api.IndonesianRecipeApiService
import com.example.foodsaver.data.remote.api.SystemPrompts
import com.example.foodsaver.data.remote.dto.MealDto
import com.example.foodsaver.data.remote.api.IndoRecipeDetailDto
import com.example.foodsaver.data.util.IngredientMapper
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
    private val db: FoodSaverDatabase
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
                dto.toDomain().copy(isFavorite = isFav)
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

        // 1. Indonesian API
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
                // Continue to next source
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
                        val recipeIngredients = dto.getIngredients()
                        val matchedCount = englishIngredients.count { userIng -> 
                            recipeIngredients.any { it.contains(userIng, ignoreCase = true) }
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
            // Continue to fallback
        }
        
        // 3. Local Fallback
        return@withContext try {
            val localFallback = ruleBasedEngine.generateRecommendations(ingredients, preference, prioritizeExpiring)
            Result.success(localFallback)
        } catch (e: Exception) {
            Result.failure(Exception("Resep belum bisa dimuat. Periksa koneksi internet kamu lalu coba lagi."))
        }
    }

    private fun IndoRecipeDetailDto.toRecommendation(userIngredients: List<RecipeIngredient>): RecipeRecommendation {
        val recipeIngredients = ingredient ?: emptyList()
        val used = userIngredients.filter { userIng ->
            recipeIngredients.any { it.contains(userIng.name, ignoreCase = true) }
        }.map { it.name }
        
        val optional = recipeIngredients.filter { recIng ->
            !userIngredients.any { recIng.contains(it.name, ignoreCase = true) }
        }

        val cleanedSteps = cleanRecipeInstructionsFromList(step ?: emptyList())

        return RecipeRecommendation(
            title = title,
            description = "Resep Indonesia",
            usedIngredients = used,
            optionalIngredients = optional,
            cookingTimeMinutes = times?.filter { it.isDigit() }?.toIntOrNull() ?: (cleanedSteps.size * 3) + 5,
            difficulty = difficulty ?: when {
                cleanedSteps.size < 5 -> "Mudah"
                cleanedSteps.size < 10 -> "Sedang"
                else -> "Sulit"
            },
            reason = "Ditemukan resep Indonesia yang cocok dengan bahan kamu.",
            steps = cleanedSteps,
            warningMessage = buildExpiryWarning(userIngredients),
            imageUrl = thumb,
            matchScore = 1.0
        )
    }

    private fun MealDto.toRecommendation(matchScore: Double, userIngredients: List<RecipeIngredient>): RecipeRecommendation {
        val recipeIngredients = getIngredients()
        val englishUserIngredients = userIngredients.map { IngredientMapper.mapToEnglish(it.name) }
        
        val used = userIngredients.filter { userIng ->
            val eng = IngredientMapper.mapToEnglish(userIng.name)
            recipeIngredients.any { it.contains(eng, ignoreCase = true) }
        }.map { it.name }
        
        val optional = recipeIngredients.filter { recIng ->
            !englishUserIngredients.any { recIng.contains(it, ignoreCase = true) }
        }

        val cleanedSteps = cleanRecipeInstructions(instructions ?: "")

        return RecipeRecommendation(
            title = name,
            description = area ?: "Resep Global", 
            usedIngredients = used,
            optionalIngredients = optional,
            cookingTimeMinutes = (cleanedSteps.size * 2) + 10,
            difficulty = when {
                cleanedSteps.size < 5 -> "Mudah"
                cleanedSteps.size < 10 -> "Sedang"
                else -> "Sulit"
            },
            reason = "Resep ini ditemukan berdasarkan bahan: ${used.joinToString(", ")}.",
            steps = cleanedSteps,
            warningMessage = buildExpiryWarning(userIngredients),
            imageUrl = thumbUrl,
            matchScore = matchScore
        )
    }

    private fun cleanRecipeInstructionsFromList(rawSteps: List<String>): List<String> {
        return rawSteps.map { it.trim() }
            .filter { it.isNotBlank() }
            .map { it.replace(Regex("^(?i)step\\s*\\d+[:.]?\\s*", RegexOption.IGNORE_CASE), "") }
            .map { it.replace(Regex("^\\d+[:.]?\\s*"), "") }
            .map { it.replace(Regex("^Langkah\\s+\\d+[:.]?\\s*", RegexOption.IGNORE_CASE), "") }
            .distinct()
    }

    private fun cleanRecipeInstructions(rawInstructions: String): List<String> {
        if (rawInstructions.isBlank()) return emptyList()
        
        val stepHeaderPattern = Regex("(?i)^step\\s*[:\\-]?\\s*(?:\\d+|one|two|three|four|five|six|seven|eight|nine|ten)[:.)]?\\s*$", RegexOption.IGNORE_CASE)
        val stepPrefixPattern = Regex("(?i)^step\\s*[:\\-]?\\s*(?:\\d+|one|two|three|four|five|six|seven|eight|nine|ten)[:.)]?\\s*", RegexOption.IGNORE_CASE)
        val numericPrefixPattern = Regex("^\\d+[:.)]\\s*")

        val refinedSteps = mutableListOf<String>()
        val lines = rawInstructions.split(Regex("\\r?\\n"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        for (line in lines) {
            if (line.matches(stepHeaderPattern)) continue
            
            var cleanedLine = line.replace(stepPrefixPattern, "")
            cleanedLine = cleanedLine.replace(numericPrefixPattern, "").trim()
            
            if (cleanedLine.isBlank()) continue
            
            if (cleanedLine.length > 160 && cleanedLine.contains(". ")) {
                val sentences = cleanedLine.split(Regex("\\.\\s+(?=[A-Z])"))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                
                for (sentence in sentences) {
                    val finalSentence = if (!sentence.endsWith(".") && sentence.length > 2) "$sentence." else sentence
                    refinedSteps.add(finalSentence)
                }
            } else {
                refinedSteps.add(cleanedLine)
            }
        }
        
        return refinedSteps
            .filter { it.length > 3 }
            .distinct()
    }

    private fun buildExpiryWarning(userIngredients: List<RecipeIngredient>): String? {
        val expiredCount = userIngredients.count { it.daysLeft != null && it.daysLeft < 0 }
        val nearExpiryCount = userIngredients.count { it.daysLeft != null && it.daysLeft in 0..3 }
        
        return when {
            expiredCount > 0 -> "Peringatan: Ada bahan yang sudah kedaluwarsa. Mohon cek kondisi bahan sebelum memasak."
            nearExpiryCount > 0 -> "Catatan FoodSaver: Bahan hampir kedaluwarsa diprioritaskan agar tidak terbuang."
            else -> null
        }
    }

    private fun MealDto.getIngredients(): List<String> {
        val list = mutableListOf<String>()
        val fields = listOf(
            strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
            strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
            strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
            strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
        )
        fields.forEach { if (!it.isNullOrBlank()) list.add(it.lowercase()) }
        return list
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
            "- ${it.name} (${it.quantity}), Sumber: ${it.source}, Status: ${it.expiryStatus ?: "Aman"}${it.daysLeft?.let { d -> ", Sisa hari: $d" } ?: ""}"
        }
        
        return """
            Berikut adalah bahan-bahan yang saya miliki:
            $ingredientList

            Preferensi saya: $preference
            Prioritaskan bahan hampir expired: ${if (prioritizeExpiring) "Ya" else "Tidak"}

            Tolong buatkan rekomendasi resep terbaik.
        """.trimIndent()
    }

    private fun parseAiResponse(text: String): RecipeRecommendation {
        val lines = text.lines()
        
        fun getValue(header: String): String {
            val startIndex = lines.indexOfFirst { it.startsWith(header, ignoreCase = true) }
            if (startIndex == -1) return ""
            
            val result = mutableListOf<String>()
            for (i in startIndex + 1 until lines.size) {
                if (lines[i].contains(":") && !lines[i].startsWith("-") && !lines[i].getOrNull(0)?.isDigit()!!) break
                if (lines[i].isNotBlank()) result.add(lines[i].trim())
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
            warningMessage = if (note.isNotBlank()) note else null
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
