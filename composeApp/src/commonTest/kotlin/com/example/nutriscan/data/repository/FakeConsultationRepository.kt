package com.example.nutriscan.data.repository

import com.example.nutriscan.domain.model.ChatMessage
import com.example.nutriscan.domain.model.Conversation
import com.example.nutriscan.domain.model.Nutritionist
import com.example.nutriscan.domain.model.UserRole
import com.example.nutriscan.domain.repository.ConsultationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeConsultationRepository : ConsultationRepository {

    private val nutritionists = listOf(
        Nutritionist(
            id = "nutri-1",
            name = "Dr. Sinta",
            specialty = "Gizi Klinik",
            bio = "Ahli gizi klinik",
            experienceYears = 5,
            rating = 4.8,
            reviewCount = 120,
            pricePerChat = 30
        ),
        Nutritionist(
            id = "nutri-2",
            name = "Dr. Budi",
            specialty = "Gizi Olahraga",
            bio = "Ahli gizi olahraga",
            experienceYears = 4,
            rating = 4.7,
            reviewCount = 90,
            pricePerChat = 20
        )
    )

    private val conversations = MutableStateFlow<List<Conversation>>(emptyList())
    private val messages = MutableStateFlow<List<ChatMessage>>(emptyList())

    private var nextConversationId = 1L
    private var nextMessageId = 1L

    override fun getNutritionists(): List<Nutritionist> = nutritionists

    override fun getNutritionist(id: String): Nutritionist? =
        nutritionists.find { it.id == id }

    override fun observeConversations(): Flow<List<Conversation>> = conversations

    override fun observeConversationsForUser(userName: String): Flow<List<Conversation>> =
        conversations.map { list -> list.filter { it.userName == userName } }

    override fun observeMessages(conversationId: Long): Flow<List<ChatMessage>> =
        messages.map { list -> list.filter { it.conversationId == conversationId } }

    override suspend fun getConversation(conversationId: Long): Conversation? =
        conversations.value.find { it.id == conversationId }

    override suspend fun startOrGetConversation(
        nutritionistId: String,
        nutritionistName: String,
        nutritionistSpecialty: String,
        userName: String
    ): Conversation {
        val existing = conversations.value.find {
            it.nutritionistId == nutritionistId && it.userName == userName
        }

        if (existing != null) return existing

        val now = 1_000L
        val conversation = Conversation(
            id = nextConversationId++,
            nutritionistId = nutritionistId,
            nutritionistName = nutritionistName,
            nutritionistSpecialty = nutritionistSpecialty,
            userName = userName,
            createdAt = now,
            lastMessage = "Halo, ada yang bisa dibantu?",
            lastMessageAt = now
        )

        conversations.value = conversations.value + conversation
        return conversation
    }

    override suspend fun sendMessage(
        conversationId: Long,
        senderRole: UserRole,
        content: String
    ): ChatMessage {
        val message = ChatMessage(
            id = nextMessageId++,
            conversationId = conversationId,
            sender = senderRole,
            content = content,
            timestamp = 1_000L + nextMessageId
        )

        messages.value = messages.value + message
        return message
    }

    fun addConversation(conversation: Conversation) {
        conversations.value = conversations.value + conversation
    }

    fun addMessage(message: ChatMessage) {
        messages.value = messages.value + message
    }

    fun currentConversations(): List<Conversation> = conversations.value

    fun currentMessages(): List<ChatMessage> = messages.value
}