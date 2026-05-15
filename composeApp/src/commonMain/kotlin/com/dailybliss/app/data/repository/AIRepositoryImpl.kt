package com.dailybliss.app.data.repository

import com.dailybliss.app.core.util.toBase64
import com.dailybliss.app.data.local.datastore.UserPreferences
import com.dailybliss.app.data.remote.api.GeminiService
import com.dailybliss.app.data.remote.api.SystemPrompts
import com.dailybliss.app.data.remote.dto.GeminiContent
import com.dailybliss.app.data.remote.dto.GeminiInlineData
import com.dailybliss.app.data.remote.dto.GeminiPart
import com.dailybliss.app.domain.repository.AIRepository
import com.dailybliss.app.presentation.screens.ai.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AIRepositoryImpl(
    private val geminiService: GeminiService,
    private val userPreferences: UserPreferences
) : AIRepository {
    
    override suspend fun streamChat(messages: List<ChatMessage>): Flow<String> {
        val geminiContents = mapToGeminiContents(messages)
        return geminiService.streamContent(
            contents = geminiContents,
            systemInstruction = getDynamicSystemPrompt()
        )
    }

    override suspend fun chat(messages: List<ChatMessage>): String {
        val geminiContents = mapToGeminiContents(messages)
        return geminiService.generateChat(
            contents = geminiContents,
            systemInstruction = getDynamicSystemPrompt()
        ).getOrThrow()
    }

    private suspend fun getDynamicSystemPrompt(): String {
    val nickname = userPreferences.nickname.first()
    val style = userPreferences.aiLanguageStyle.first()
    
    return """
        ${SystemPrompts.CHAT_SYSTEM_PROMPT}
        
        PANDUAN KHUSUS UNTUK $nickname:
        - Kamu sedang berbicara dengan: $nickname.
        - Gaya bahasa WAJIB: $style.
        
        DEFINISI GAYA BAHASA '$style' (Ikuti dengan ketat):
        1. 'Santai/Kasual': Gunakan bahasa percakapan sehari-hari yang akrab namun sopan. Boleh gunakan kata seperti 'banget', 'kok', 'sih'. Hindari bahasa yang terlalu alay/lebay. Anggap $nickname adalah teman dekat.
        2. 'Formal/Baku': Gunakan kosakata bahasa Indonesia yang standar (EYD). Gunakan kalimat yang lengkap dan tertata. Tetap hangat, tapi pertahankan profesionalisme. Cocok untuk refleksi serius.
        3. 'Puitis/Puitik': Gunakan diksi yang indah, lembut, dan penuh makna. Gunakan sedikit metafora alam atau perasaan. Fokus pada ketenangan dan keindahan momen kecil.
        4. 'Motivasi': Gunakan nada yang energetik dan inspiratif. Berikan penekanan pada potensi diri dan afirmasi positif. Fokus pada solusi dan semangat untuk hari esok.
        
        CATATAN PENTING:
        - Jangan berlebihan (jangan 'lebay'). Tetaplah terasa natural seperti manusia, bukan AI yang dipaksakan.
        - Pastikan perbedaan antara gaya 'Santai' dan 'Puitis' sangat terasa jelas dari pilihan kata (diksi).
        - Selalu panggil nama '$nickname' dalam responmu agar terasa personal.
    """.trimIndent()
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
