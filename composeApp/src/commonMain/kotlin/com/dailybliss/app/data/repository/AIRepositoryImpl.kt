package com.dailybliss.app.data.repository

import com.dailybliss.app.core.util.toBase64
import com.dailybliss.app.data.remote.api.GeminiService
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
        val geminiContents = messages.map { chatMessage ->
            val parts = mutableListOf<GeminiPart>()
            
            // Add image part if present (Base64)
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

            // Add text part (Required if image is present or as standalone)
            // Some models require at least one text part
            val textContent = if (chatMessage.text.isBlank() && chatMessage.imageBytes != null) {
                "Jelaskan gambar ini." // Fallback text for image-only messages
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
        
        return geminiService.streamContent(geminiContents)
    }
}
