package com.dailybliss.app.data.repository

import com.dailybliss.app.core.util.toBase64
import com.dailybliss.app.data.remote.api.GeminiService
import com.dailybliss.app.data.remote.api.SystemPrompts
import com.dailybliss.app.data.remote.dto.GeminiContent
import com.dailybliss.app.data.remote.dto.GeminiInlineData
import com.dailybliss.app.data.remote.dto.GeminiPart
import com.dailybliss.app.domain.repository.AIRepository
import com.dailybliss.app.presentation.screens.ai.ChatMessage
import kotlinx.coroutines.flow.Flow

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {
    
    override suspend fun streamChat(messages: List<ChatMessage>): Flow<String> {
        val geminiContents = mapToGeminiContents(messages)
        return geminiService.streamContent(
            contents = geminiContents,
            systemInstruction = SystemPrompts.CHAT_SYSTEM_PROMPT
        )
    }

    override suspend fun chat(messages: List<ChatMessage>): String {
        val geminiContents = mapToGeminiContents(messages)
        return geminiService.generateChat(
            contents = geminiContents,
            systemInstruction = SystemPrompts.CHAT_SYSTEM_PROMPT
        ).getOrThrow()
    }

    private fun mapToGeminiContents(messages: List<ChatMessage>): List<GeminiContent> {
        return messages.map { chatMessage ->
            val parts = mutableListOf<GeminiPart>()
            
            chatMessage.imageBytes?.let { bytes ->
                parts.add(
                    GeminiPart(
                        inline_data = GeminiInlineData(
                            mime_type = "image/jpeg",
                            data = bytes.toBase64()
                        )
                    )
                )
            }

            val textContent = if (chatMessage.text.isBlank() && chatMessage.imageBytes != null) {
                "Jelaskan gambar ini."
            } else {
                chatMessage.text
            }
            
            if (textContent.isNotBlank()) {
                parts.add(GeminiPart(text = textContent))
            }
            
            GeminiContent(
                parts = parts,
                role = if (chatMessage.role == "user") "user" else "model"
            )
        }
    }
}
