package com.example.masakuy.data.remote.api

import com.example.masakuy.data.remote.dto.GeminiRequest
import com.example.masakuy.data.remote.dto.GeminiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GeminiService(private val httpClient: HttpClient) {

    suspend fun getRecommendation(
        budget: Int,
        ingredients: List<String>
    ): List<String> {
        val prompt = buildPrompt(budget, ingredients)

        return try {
            val response = httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(
                    contents = listOf(
                        mapOf("parts" to listOf(mapOf("text" to prompt)))
                    )
                ))
            }.body<GeminiResponse>()

            // Parse response dan extract recipe recommendations
            extractRecipeNames(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun buildPrompt(budget: Int, ingredients: List<String>): String {
        val ingredientList = if (ingredients.isNotEmpty()) {
            "Available ingredients: ${ingredients.joinToString(", ")}"
        } else {
            ""
        }

        return """
            Berikan 5 rekomendasi resep masakan Indonesia yang sehat, mudah dibuat, 
            dan cocok untuk budget Rp${budget} dengan estimasi total biaya per porsi.
            
            $ingredientList
            
            Format jawaban:
            1. [Nama Resep] - Rp[Harga]
            2. [Nama Resep] - Rp[Harga]
            ...dst
            
            Jangan sertakan penjelasan, hanya berikan daftar.
        """.trimIndent()
    }

    private fun extractRecipeNames(response: GeminiResponse): List<String> {
        return try {
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            text.split("\n")
                .filter { it.isNotEmpty() }
                .mapNotNull { line ->
                    val match = Regex("""^\d+\.\s*(.+?)\s*-\s*Rp[\d.]+""").find(line)
                    match?.groupValues?.getOrNull(1)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}