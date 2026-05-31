package com.kelazzz.app.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Domain model untuk pesan chat AI
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val role: ChatRole,
    val timestamp: Long,
    val isLoading: Boolean = false
) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun userMessage(content: String): ChatMessage = ChatMessage(
            id = Uuid.random().toString(),
            content = content,
            role = ChatRole.USER,
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )

        @OptIn(ExperimentalUuidApi::class)
        fun assistantMessage(content: String): ChatMessage = ChatMessage(
            id = Uuid.random().toString(),
            content = content,
            role = ChatRole.ASSISTANT,
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )

        @OptIn(ExperimentalUuidApi::class)
        fun loadingMessage(): ChatMessage = ChatMessage(
            id = Uuid.random().toString(),
            content = "",
            role = ChatRole.ASSISTANT,
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
            isLoading = true
        )
    }
}

/**
 * Role dalam percakapan chat
 */
enum class ChatRole {
    USER,
    ASSISTANT
}
