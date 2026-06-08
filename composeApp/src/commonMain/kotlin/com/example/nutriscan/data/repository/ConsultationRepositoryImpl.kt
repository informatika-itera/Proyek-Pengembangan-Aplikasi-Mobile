package com.example.nutriscan.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.nutriscan.data.local.NutriScanDatabase
import com.example.nutriscan.data.local.entity.ChatMapper.toDomain
import com.example.nutriscan.domain.model.ChatMessage
import com.example.nutriscan.domain.model.Conversation
import com.example.nutriscan.domain.model.Nutritionist
import com.example.nutriscan.domain.model.UserRole
import com.example.nutriscan.domain.repository.ConsultationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ConsultationRepositoryImpl(
    private val database: NutriScanDatabase
) : ConsultationRepository {

    private val queries = database.consultationQueries

    private val catalog: List<Nutritionist> = listOf(
        Nutritionist(
            id = "nut_sinta",
            name = "Dr. Sinta Wijaya",
            specialty = "Gizi Klinik & Diabetes",
            bio = "Spesialis gizi klinik dengan fokus pada manajemen diabetes dan pola makan seimbang.",
            experienceYears = 8,
            rating = 4.9,
            reviewCount = 214,
            pricePerChat = 30
        ),
        Nutritionist(
            id = "nut_bagas",
            name = "Dr. Bagas Pratama",
            specialty = "Gizi Olahraga",
            bio = "Membantu atlet dan penggiat fitness menyusun nutrisi untuk performa optimal.",
            experienceYears = 6,
            rating = 4.8,
            reviewCount = 168,
            pricePerChat = 25
        ),
        Nutritionist(
            id = "nut_maya",
            name = "Dr. Maya Lestari",
            specialty = "Gizi Anak & Keluarga",
            bio = "Ahli gizi keluarga yang berpengalaman menangani nutrisi anak dan ibu hamil.",
            experienceYears = 10,
            rating = 5.0,
            reviewCount = 301,
            pricePerChat = 35
        ),
        Nutritionist(
            id = "nut_rian",
            name = "Rian Anggara, S.Gz",
            specialty = "Manajemen Berat Badan",
            bio = "Pendampingan diet sehat untuk menurunkan atau menjaga berat badan ideal.",
            experienceYears = 4,
            rating = 4.7,
            reviewCount = 92,
            pricePerChat = 18
        ),
        Nutritionist(
            id = "nut_dewi",
            name = "Dewi Anggraini, S.Gz",
            specialty = "Hipertensi & Jantung",
            bio = "Konsultasi pola makan rendah garam untuk penderita hipertensi dan jantung.",
            experienceYears = 5,
            rating = 4.8,
            reviewCount = 130,
            pricePerChat = 22
        ),
        Nutritionist(
            id = "nut_fajar",
            name = "Fajar Nugroho, S.Gz",
            specialty = "Gizi Umum",
            bio = "Tips nutrisi harian praktis untuk gaya hidup sehat sehari-hari.",
            experienceYears = 3,
            rating = 4.6,
            reviewCount = 64,
            pricePerChat = 15
        )
    )

    override fun getNutritionists(): List<Nutritionist> = catalog

    override fun getNutritionist(id: String): Nutritionist? =
        catalog.find { it.id == id }

    override fun observeConversations(): Flow<List<Conversation>> =
        queries.getAllConversations()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override fun observeConversationsForUser(userName: String): Flow<List<Conversation>> =
        observeConversations().map { conversations ->
            conversations.filter { it.userName == userName }
        }

    override fun observeMessages(conversationId: Long): Flow<List<ChatMessage>> =
        queries.getMessages(conversationId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getConversation(conversationId: Long): Conversation? =
        withContext(Dispatchers.Default) {
            queries.getConversationById(conversationId)
                .executeAsOneOrNull()
                ?.toDomain()
        }

    override suspend fun startOrGetConversation(
        nutritionistId: String,
        nutritionistName: String,
        nutritionistSpecialty: String,
        userName: String
    ): Conversation = withContext(Dispatchers.Default) {
        queries.transactionWithResult {
            val existing = queries
                .getConversationByPair(nutritionistId, userName)
                .executeAsOneOrNull()

            if (existing != null) {
                existing.toDomain()
            } else {
                val now = Clock.System.now().toEpochMilliseconds()
                val welcome = "Halo! Saya $nutritionistName. " +
                        "Ada yang bisa saya bantu seputar nutrisi & pola makan Anda hari ini?"

                queries.insertConversation(
                    nutritionist_id = nutritionistId,
                    nutritionist_name = nutritionistName,
                    nutritionist_specialty = nutritionistSpecialty,
                    user_name = userName,
                    created_at = now,
                    last_message = welcome,
                    last_message_at = now
                )

                val conversationId = queries.lastInsertRowId().executeAsOne()

                queries.insertMessage(
                    conversation_id = conversationId,
                    sender = UserRole.NUTRITIONIST.name,
                    content = welcome,
                    timestamp = now
                )

                Conversation(
                    id = conversationId,
                    nutritionistId = nutritionistId,
                    nutritionistName = nutritionistName,
                    nutritionistSpecialty = nutritionistSpecialty,
                    userName = userName,
                    createdAt = now,
                    lastMessage = welcome,
                    lastMessageAt = now
                )
            }
        }
    }

    override suspend fun sendMessage(
        conversationId: Long,
        senderRole: UserRole,
        content: String
    ): ChatMessage = withContext(Dispatchers.Default) {
        queries.transactionWithResult {
            val now = Clock.System.now().toEpochMilliseconds()

            queries.insertMessage(
                conversation_id = conversationId,
                sender = senderRole.name,
                content = content,
                timestamp = now
            )

            val messageId = queries.lastInsertRowId().executeAsOne()

            queries.updateConversationLastMessage(
                last_message = content,
                last_message_at = now,
                id = conversationId
            )

            ChatMessage(
                id = messageId,
                conversationId = conversationId,
                sender = senderRole,
                content = content,
                timestamp = now
            )
        }
    }
}