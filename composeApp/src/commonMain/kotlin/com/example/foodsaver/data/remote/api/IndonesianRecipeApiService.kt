package com.example.foodsaver.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

@Serializable
data class IndoRecipeResponse(
    val status: Boolean,
    val method: String,
    val results: List<IndoRecipeDto>
)

@Serializable
data class IndoRecipeDto(
    val title: String,
    val thumb: String? = null,
    val key: String,
    val times: String? = null,
    val serving: String? = null,
    val difficulty: String? = null
)

@Serializable
data class IndoRecipeDetailResponse(
    val status: Boolean,
    val results: IndoRecipeDetailDto
)

@Serializable
data class IndoRecipeDetailDto(
    val title: String,
    val thumb: String? = null,
    val servings: String? = null,
    val times: String? = null,
    val difficulty: String? = null,
    val author: IndoAuthorDto? = null,
    val desc: String? = null,
    val needItem: List<IndoNeedItemDto>? = null,
    val ingredient: List<String>? = null,
    val step: List<String>? = null
)

@Serializable
data class IndoAuthorDto(
    val user: String? = null,
    val datePublished: String? = null
)

@Serializable
data class IndoNeedItemDto(
    val item_name: String? = null,
    val thumb_item: String? = null
)

/**
 * Service for Indonesian Recipe API.
 */
class IndonesianRecipeApiService(private val client: HttpClient) {
    companion object {
        private const val BASE_URL = "https://masak-apa-tom.vercel.app/api"
    }

    suspend fun searchByIngredient(query: String): IndoRecipeResponse {
        return client.get("$BASE_URL/search/") {
            parameter("q", query)
        }.body()
    }

    suspend fun getRecipeDetails(key: String): IndoRecipeDetailResponse {
        return client.get("$BASE_URL/recipe/$key").body()
    }
}
