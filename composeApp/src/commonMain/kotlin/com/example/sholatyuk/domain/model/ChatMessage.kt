package com.example.sholatyuk.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ChatMessage(
    val id: Long = 0,
    val content: String,
    val role: MessageRole,
    val timestamp: Instant = Clock.System.now(),
    val isError: Boolean = false
)

enum class MessageRole {
    USER,
    ASSISTANT
}
