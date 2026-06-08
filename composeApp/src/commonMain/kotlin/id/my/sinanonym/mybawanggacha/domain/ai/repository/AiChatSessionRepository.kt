package id.my.sinanonym.mybawanggacha.domain.ai.repository

interface AiChatSessionRepository {
    suspend fun getMessages(sessionKey: String): List<ChatMessage>
    suspend fun appendMessage(sessionKey: String, message: ChatMessage)
    suspend fun clearSession(sessionKey: String)
}
