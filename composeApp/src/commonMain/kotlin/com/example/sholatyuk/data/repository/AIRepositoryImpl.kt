package com.example.sholatyuk.data.repository

import com.example.sholatyuk.data.local.SholatYukDatabase
import com.example.sholatyuk.data.local.entity.toDomainList
import com.example.sholatyuk.data.local.entity.roleKey
import com.example.sholatyuk.data.local.entity.timestampMillis
import com.example.sholatyuk.data.local.entity.isErrorAsLong
import com.example.sholatyuk.data.remote.api.GeminiService
import com.example.sholatyuk.domain.model.ChatMessage
import com.example.sholatyuk.domain.repository.AIRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AIRepositoryImpl(
    database: SholatYukDatabase,
    private val geminiService: GeminiService
) : AIRepository {

    private val queries = database.chatHistoryQueries

    // ── Read ──────────────────────────────────────────────────────

    override fun getChatHistory(): Flow<List<ChatMessage>> =
        queries.getChatHistory()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }

    // ── AI Call ───────────────────────────────────────────────────

    override suspend fun askIslamAI(question: String): Result<String> {
        return try {
            // Memanggil fungsi generateResponse yang ada di GeminiService.kt
            val answer = geminiService.generateResponse(prompt = question)
            Result.success(answer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Write ─────────────────────────────────────────────────────

    override suspend fun saveMessage(message: ChatMessage) {
        queries.insertMessage(
            content   = message.content,
            role      = message.roleKey(),
            timestamp = message.timestampMillis(),
            is_error  = message.isErrorAsLong()
        )
    }

    override suspend fun clearHistory() {
        queries.clearHistory()
    }
}