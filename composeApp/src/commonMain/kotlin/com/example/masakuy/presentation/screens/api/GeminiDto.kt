package com.example.masakuy.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<Map<String, Any>>
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>? = null
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class RecipeDto(
    val id: String,
    val name: String,
    val image: String,
    val estimatedCost: Int,
    val estimatedTime: Int,
    val difficulty: String,
    val ingredients: List<IngredientDto>,
    val instructions: List<String>
)

@Serializable
data class IngredientDto(
    val name: String,
    val quantity: String,
    val estimatedPrice: Int
)