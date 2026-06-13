package com.example.foodsaver.data.mapper

import com.example.foodsaver.data.remote.model.MealDto
import com.example.foodsaver.data.remote.model.IndoRecipeDetailDto
import com.example.foodsaver.domain.model.*

/**
 * Mappers for Recipe related DTOs
 */

fun MealDto.toDomain(isFavorite: Boolean = false): Recipe {
    val ingredientsList = mutableListOf<IngredientAmount>()
    
    val ingredientNames = listOf(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
        strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
        strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
    )
    val ingredientMeasures = listOf(
        strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
        strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
        strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
        strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
    )
    
    for (i in ingredientNames.indices) {
        val name = ingredientNames[i]
        val measure = ingredientMeasures[i]
        if (!name.isNullOrBlank()) {
            ingredientsList.add(IngredientAmount(name, measure ?: ""))
        }
    }

    return Recipe(
        id = id,
        name = name,
        imageUrl = thumbUrl ?: "",
        category = category,
        area = area,
        instructions = instructions,
        ingredients = ingredientsList,
        isFavorite = isFavorite
    )
}

fun IndoRecipeDetailDto.toRecommendation(userIngredients: List<RecipeIngredient>): RecipeRecommendation {
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
        cookingTimeMinutes = times?.filter { it.isDigit() }?.toIntOrNull() ?: (cleanedSteps.size * 3 + 5),
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

fun MealDto.toRecommendation(matchScore: Double, userIngredients: List<RecipeIngredient>): RecipeRecommendation {
    val recipeIngredients = getIngredientsList()
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

fun MealDto.getIngredientsList(): List<String> {
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
    
    val stepPrefixPattern = Regex("(?i)^step\\s*[:\\-]?\\s*(?:\\d+|one|two|three|four|five|six|seven|eight|nine|ten)[:.)]?\\s*", RegexOption.IGNORE_CASE)
    val numericPrefixPattern = Regex("^\\d+[:.)]\\s*")

    return rawInstructions.split(Regex("\\r?\\n"))
        .map { it.trim() }
        .filter { it.isNotBlank() && !it.matches(Regex("(?i)^step\\s*[:\\-]?\\s*(?:\\d+|one|two|three|four|five|six|seven|eight|nine|ten)[:.)]?\\s*$", RegexOption.IGNORE_CASE)) }
        .flatMap { line ->
            var cleaned = line.replace(stepPrefixPattern, "")
            cleaned = cleaned.replace(numericPrefixPattern, "").trim()
            
            if (cleaned.length > 160 && cleaned.contains(". ")) {
                cleaned.split(Regex("\\.\\s+(?=[A-Z])"))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map { if (!it.endsWith(".") && it.length > 2) "$it." else it }
            } else {
                listOf(cleaned)
            }
        }
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
