package com.kelazzz.app.data.repository

import com.kelazzz.app.data.remote.ai.ChatToolHandler
import com.kelazzz.app.data.remote.ai.OpenCodeChatMessage
import com.kelazzz.app.data.remote.ai.OpenCodeGoService
import com.kelazzz.app.data.remote.ai.SystemPrompts
import com.kelazzz.app.domain.model.ChatMessage
import com.kelazzz.app.domain.model.ChatRole
import com.kelazzz.app.domain.repository.AIRepository

/**
 * Implementasi AIRepository — menghubungkan OpenCode Go API dengan tool calling
 *
 * Flow chat:
 * 1. User kirim pesan
 * 2. ChatToolHandler deteksi intent dan ambil data relevan dari repository
 * 3. Data di-inject sebagai konteks tambahan ke prompt
 * 4. OpenCodeGoService kirim ke API dengan system prompt + conversation history
 * 5. Return respons AI
 *
 * Prinsip:
 * - AI tidak membaca database langsung
 * - Semua data diambil via tool (ChatToolHandler → Repository)
 * - Conversation history dikirim untuk konteks multi-turn
 */
class AIRepositoryImpl(
    private val openCodeGoService: OpenCodeGoService,
    private val toolHandler: ChatToolHandler
) : AIRepository {

    override suspend fun analyzeAttendance(attendanceData: String): Result<String> {
        return openCodeGoService.generateContent(
            prompt = attendanceData,
            systemPrompt = SystemPrompts.ATTENDANCE_ANALYZER
        )
    }

    override suspend fun chat(
        message: String,
        history: List<ChatMessage>
    ): Result<String> {
        return try {
            // 1. Tool calling: deteksi intent dan ambil data yang relevan
            val toolContext = toolHandler.detectAndFetchContext(message)

            // 2. Susun prompt yang diperkaya dengan data dari tool
            val enrichedPrompt = if (toolContext.isNotBlank()) {
                buildString {
                    appendLine("Berikut adalah data akademik pengguna yang diambil dari sistem:")
                    appendLine()
                    appendLine(toolContext)
                    appendLine()
                    appendLine("Pertanyaan pengguna:")
                    appendLine(message)
                }
            } else {
                message
            }

            // 3. Bangun conversation history untuk multi-turn context
            val conversationHistory = buildConversationHistory(history)

            // 4. Kirim ke OpenCode Go API
            openCodeGoService.generateContentWithHistory(
                prompt = enrichedPrompt,
                systemPrompt = SystemPrompts.ACADEMIC_ASSISTANT,
                history = conversationHistory
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearHistory() {
        // History dikelola di ViewModel, bukan di repository
        // Method ini sebagai hook jika di masa depan perlu clear cache di repository
    }

    /**
     * Konversi ChatMessage list menjadi format OpenCode untuk multi-turn chat
     */
    private fun buildConversationHistory(history: List<ChatMessage>): List<OpenCodeChatMessage> {
        return history
            .filter { !it.isLoading && it.content.isNotBlank() }
            .map { message ->
                OpenCodeChatMessage(
                    content = message.content,
                    role = when (message.role) {
                        ChatRole.USER -> "user"
                        ChatRole.ASSISTANT -> "assistant"
                    }
                )
            }
    }
}
