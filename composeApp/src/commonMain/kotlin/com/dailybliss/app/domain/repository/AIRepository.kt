package com.dailybliss.app.domain.repository

import com.dailybliss.app.presentation.screens.ai.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    /**
     * Streams a chat response from the AI assistant.
     * Supports multi-turn conversation and multimodal input (text + images).
     */
    suspend fun streamChat(messages: List<ChatMessage>): Flow<String>

    /**
     * Gets a full chat response from the AI assistant (non-streaming).
     * Supports multi-turn conversation and multimodal input.
     */
    suspend fun chat(messages: List<ChatMessage>): String
}
