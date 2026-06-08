package id.my.sinanonym.mybawanggacha.data.repository.ai

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import id.my.sinanonym.mybawanggacha.data.local.NoteDatabase
import id.my.sinanonym.mybawanggacha.domain.ai.repository.AiChatSessionRepository
import id.my.sinanonym.mybawanggacha.domain.ai.repository.ChatMessage
import id.my.sinanonym.mybawanggacha.domain.ai.repository.MessageSender
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class AiChatSessionRepositoryImpl(
    database: NoteDatabase,
    private val dispatchers: AppDispatchers
) : AiChatSessionRepository {
    private val queries = database.aiChatQueries

    override suspend fun getMessages(sessionKey: String): List<ChatMessage> = withContext(dispatchers.io) {
        queries.getAiChatMessages(sessionKey)
            .executeAsList()
            .map { entity ->
                ChatMessage(
                    sender = MessageSender.entries.firstOrNull { sender ->
                        sender.name.equals(entity.sender, ignoreCase = true)
                    } ?: MessageSender.AI,
                    text = entity.text
                )
            }
    }

    override suspend fun appendMessage(
        sessionKey: String,
        message: ChatMessage
    ): Unit = withContext(dispatchers.io) {
        queries.insertAiChatMessage(
            session_key = sessionKey,
            sender = message.sender.name,
            text = message.text,
            created_at = Clock.System.now().toEpochMilliseconds()
        )
        Unit
    }

    override suspend fun clearSession(sessionKey: String): Unit = withContext(dispatchers.io) {
        queries.deleteAiChatSession(sessionKey)
        Unit
    }
}
