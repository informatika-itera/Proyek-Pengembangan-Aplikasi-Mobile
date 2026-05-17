package com.example.masakuy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String,
    val name: String,
    val image: String,
    val estimatedCost: Int,
    val estimatedTime: Int,
    val difficulty: String,
    val isFavorite: Boolean = false
)

@Serializable
data class RecipeDetail(
    val id: String,
    val name: String,
    val image: String,
    val estimatedCost: Int,
    val estimatedTime: Int,
    val difficulty: String,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val isFavorite: Boolean = false
)

@Serializable
data class Ingredient(
    val name: String,
    val quantity: String,
    val estimatedPrice: Int
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String
)