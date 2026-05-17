package com.example.masakuy.domain.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    fun getRecommendation(
        budget: Int,
        ingredients: List<String> = emptyList(),
        preferences: String = ""
    ): Flow<Result<List<Recipe>>>
}