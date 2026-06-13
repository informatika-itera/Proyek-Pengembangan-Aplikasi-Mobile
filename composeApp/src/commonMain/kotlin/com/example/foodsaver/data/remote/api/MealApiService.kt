package com.example.foodsaver.data.remote.api

import com.example.foodsaver.data.remote.model.MealResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MealApiService(private val client: HttpClient) {
    companion object {
        private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1"
    }

    suspend fun searchByIngredient(ingredient: String): MealResponse {
        return client.get("$BASE_URL/filter.php") {
            parameter("i", ingredient)
        }.body()
    }

    suspend fun getRecipeDetails(id: String): MealResponse {
        return client.get("$BASE_URL/lookup.php") {
            parameter("i", id)
        }.body()
    }

    suspend fun searchByName(query: String): MealResponse {
        return client.get("$BASE_URL/search.php") {
            parameter("s", query)
        }.body()
    }
}
