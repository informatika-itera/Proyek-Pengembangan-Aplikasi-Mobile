package com.example.foodsaver.data.remote.api

import com.example.foodsaver.data.remote.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Service for Indonesian Recipe API.
 */
class IndonesianRecipeApiService(private val client: HttpClient) {
    companion object {
        private const val BASE_URL = "https://masak-apa-tom.vercel.app/api"
    }

    suspend fun searchByIngredient(query: String): IndoRecipeResponse {
        return client.get("${BASE_URL}/search/") {
            parameter("q", query)
        }.body()
    }

    suspend fun getRecipeDetails(key: String): IndoRecipeDetailResponse {
        return client.get("${BASE_URL}/recipe/$key").body()
    }
}
