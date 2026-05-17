// ── AIRepository.kt ──────────────────────────────────────
package com.example.sholatyuk.domain.repository

import com.example.sholatyuk.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    fun getChatHistory(): Flow<List<ChatMessage>>
    suspend fun askIslamAI(question: String): Result<String>
    suspend fun saveMessage(message: ChatMessage)
    suspend fun clearHistory()
}
