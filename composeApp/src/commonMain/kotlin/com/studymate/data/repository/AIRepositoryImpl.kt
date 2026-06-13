package com.studymate.data.repository

import com.studymate.core.network.ApiConstants
import com.studymate.domain.repository.AIRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class AIRepositoryImpl(
    private val client: HttpClient,
    private val groqApiKey: String
) : AIRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun refineNote(subject: String, title: String, content: String): Result<String> {
        val prompt = ApiConstants.Prompts.refineNote(subject, title, content)
        return queryGroq(prompt)
    }

    override suspend fun generateQuiz(subject: String, title: String, noteContent: String, questionCount: Int): Result<String> {
        val prompt = ApiConstants.Prompts.generateQuiz(subject, title, noteContent, questionCount)
        return queryGroq(prompt)
    }

    private suspend fun queryGroq(prompt: String): Result<String> {
        return try {
            val url = "https://api.groq.com/openai/v1/chat/completions"
            
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $groqApiKey")
                setBody(GroqRequest(
                    model = "llama-3.3-70b-versatile",
                    messages = listOf(GroqMessage(role = "user", content = prompt))
                ))
            }

            if (!response.status.isSuccess()) {
                val errorMsg = response.bodyAsText()
                println("Groq API Error Detail: $errorMsg")
                return Result.failure(Exception("Groq Error: ${response.status.value}"))
            }

            val groqResponse: GroqResponse = response.body()
            val text = groqResponse.choices.firstOrNull()?.message?.content
            
            println("Groq Response Text: $text") // Debug log
            
            if (text != null) Result.success(text)
            else Result.failure(Exception("AI tidak memberikan respon."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateMantra(): Result<String> {
        val prompt = "Berikan satu kalimat motivasi belajar yang sangat singkat dan inspiratif dalam Bahasa Indonesia."
        return queryGroq(prompt)
    }
}

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponse(
    val choices: List<GroqChoice>
)

@Serializable
data class GroqChoice(
    val message: GroqMessage
)
