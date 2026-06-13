package com.example.fitgen.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqChatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens")
    val maxTokens: Int = 1000
)

@Serializable
data class GroqChoice(
    val index: Int,
    val message: GroqMessage,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class GroqChatResponse(
    val id: String? = null,
    val choices: List<GroqChoice> = emptyList(),
    val created: Long? = null
)

// Helper extension function
fun GroqChatResponse.getTextContent(): String? {
    return choices.firstOrNull()?.message?.content
}

// Vision DTOs
@Serializable
data class GroqVisionMessage(
    val role: String,
    val content: List<GroqContentPart>
)

@Serializable
data class GroqContentPart(
    val type: String,
    val text: String? = null,
    @SerialName("image_url")
    val imageUrl: GroqImageUrl? = null
)

@Serializable
data class GroqImageUrl(
    val url: String
)

@Serializable
data class GroqVisionChatRequest(
    val model: String,
    val messages: List<GroqVisionMessage>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens")
    val maxTokens: Int = 1000
)
