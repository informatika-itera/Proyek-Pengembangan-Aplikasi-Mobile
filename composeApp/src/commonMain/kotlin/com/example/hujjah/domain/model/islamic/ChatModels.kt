package com.example.hujjah.domain.model.islamic

import kotlinx.serialization.Serializable

@Serializable
enum class Sender {
    USER, AI
}

@Serializable
data class ChatMessage(
    val id: String,
    val sender: Sender,
    val text: String,
    val timestamp: Long,
    val references: List<IslamicReference> = emptyList(),
    val solutions: List<String> = emptyList()
)
