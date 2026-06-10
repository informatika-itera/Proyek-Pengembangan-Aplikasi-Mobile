package com.studymate.data.repository

import com.studymate.core.network.ApiConstants
import com.studymate.domain.repository.AIRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class AIRepositoryImpl(
    private val client: HttpClient,
    private val apiKey: String
) : AIRepository {

    override suspend fun refineNote(subject: String, title: String, content: String): Result<String> {
        val prompt = ApiConstants.Prompts.refineNote(subject, title, content)
        return generateContent(prompt)
    }

    override suspend fun generateQuiz(subject: String, title: String, noteContent: String): Result<String> {
        val prompt = ApiConstants.Prompts.generateQuiz(subject, title, noteContent)
        return generateContent(prompt)
    }

    private suspend fun generateContent(prompt: String): Result<String> {
        return try {
            // Using v1 (Stable) instead of v1beta to ensure compatibility
            val url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$apiKey"
            
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt))))))
            }.body()

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (text != null) Result.success(text)
            else Result.failure(Exception("AI tidak memberikan respon."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class GeminiRequest(val contents: List<Content>)
@Serializable
data class Content(val parts: List<Part>)
@Serializable
data class Part(val text: String)
@Serializable
data class GeminiResponse(val candidates: List<Candidate>? = null)
@Serializable
data class Candidate(val content: Content)
