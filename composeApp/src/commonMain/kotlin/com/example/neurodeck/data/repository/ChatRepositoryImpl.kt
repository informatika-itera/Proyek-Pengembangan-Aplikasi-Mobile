package com.example.neurodeck.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.domain.model.ChatMessage
import com.example.neurodeck.domain.model.MessageRole
import com.example.neurodeck.domain.repository.AIRepository
import com.example.neurodeck.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Implementasi ChatRepository — persist chat history di SQLDelight,
 * delegate AI calls ke AIRepository.
 *
 * Logic `sendMessage`:
 *   1. Insert user message → DB (via SQLDelight)
 *   2. Load full history → DB
 *   3. Map history ke format Gemini API (role: "user"/"model")
 *   4. Call AIRepository.chatWithHistory(history)
 *   5. Insert assistant reply → DB (success) atau error message (failure)
 *
 * Step 1 dan 5 atomik dari sisi UI: user lihat msg dia + reply (atau error)
 * via flow observer di Screen.
 */
class ChatRepositoryImpl(
    database: NeuroDeckDatabase,
    private val aiRepository: AIRepository,
) : ChatRepository {

    private val queries = database.chatMessageQueries

    override fun observeMessages(): Flow<List<ChatMessage>> =
        queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun sendMessage(userMessage: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            val trimmed = userMessage.trim()
            if (trimmed.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("Pesan tidak boleh kosong"))
            }

            // Step 1: insert user message ke DB
            val now = Clock.System.now().toEpochMilliseconds()
            queries.insert(
                role = MessageRole.User.name,
                content = trimmed,
                timestamp = now,
                isError = 0,
            )

            // Step 2: load full history (yang barusan kita save juga ikut)
            val allMessages = observeMessages().first()

            // Step 3: map ke Gemini API format
            // Role mapping: User → "user", Assistant → "model" (Gemini convention)
            val historyForApi = allMessages
                .filter { !it.isError }   // skip pesan error supaya AI tidak bingung
                .map { msg ->
                    when (msg.role) {
                        MessageRole.User -> "user" to msg.content
                        MessageRole.Assistant -> "model" to msg.content
                    }
                }

            // Step 4 & 5: call AI, save reply (atau error)
            try {
                val reply = aiRepository.chatWithHistory(historyForApi)
                queries.insert(
                    role = MessageRole.Assistant.name,
                    content = reply,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    isError = 0,
                )
                Result.success(Unit)
            } catch (e: Exception) {
                // Save error message sebagai assistant turn dengan isError=true,
                // supaya UI bisa render bubble warning + (optional future) retry button.
                val errorMsg = e.message ?: "Gagal mendapat respons dari AI"
                queries.insert(
                    role = MessageRole.Assistant.name,
                    content = "⚠️ $errorMsg",
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    isError = 1,
                )
                Result.failure(e)
            }
        }

    override suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }

    /**
     * Private mapper: ChatMessageEntity (generated SQLDelight type) → domain ChatMessage.
     */
    private fun com.example.neurodeck.data.local.ChatMessageEntity.toDomain(): ChatMessage =
        ChatMessage(
            id = id,
            role = MessageRole.fromString(role),
            content = content,
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            isError = isError == 1L,
        )
}