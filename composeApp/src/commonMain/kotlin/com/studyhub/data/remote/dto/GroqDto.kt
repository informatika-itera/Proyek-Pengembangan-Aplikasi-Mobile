package com.studyhub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1024,
    val top_p: Double = 1.0,
    val stream: Boolean = false
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponse(
    val id: String? = null,
    val choices: List<GroqChoice>? = null,
    val error: GroqError? = null
)

@Serializable
data class GroqChoice(
    val index: Int? = null,
    val message: GroqMessage,
    val finish_reason: String? = null
)

@Serializable
data class GroqError(
    val message: String? = null,
    val type: String? = null,
    val code: String? = null
)

fun GroqResponse.getTextContent(): String? {
    return choices?.firstOrNull()?.message?.content
}

fun GroqResponse.getErrorMessage(): String? {
    return error?.message
}
