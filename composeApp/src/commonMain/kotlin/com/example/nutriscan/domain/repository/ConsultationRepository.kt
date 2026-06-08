package com.example.nutriscan.domain.repository

import com.example.nutriscan.domain.model.ChatMessage
import com.example.nutriscan.domain.model.Conversation
import com.example.nutriscan.domain.model.Nutritionist
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk fitur konsultasi antara pengguna dan ahli gizi.
 */
interface ConsultationRepository {

    /** Ambil daftar semua ahli gizi yang tersedia (statis/katalog). */
    fun getNutritionists(): List<Nutritionist>

    /** Ambil satu ahli gizi berdasarkan [id], atau null jika tidak ada. */
    fun getNutritionist(id: String): Nutritionist?

    /** Observe semua percakapan (untuk tampilan ahli gizi). */
    fun observeConversations(): Flow<List<Conversation>>

    /** Observe percakapan milik pengguna tertentu. */
    fun observeConversationsForUser(userName: String): Flow<List<Conversation>>

    /** Observe pesan dalam percakapan tertentu. */
    fun observeMessages(conversationId: Long): Flow<List<ChatMessage>>

    /** Ambil satu percakapan berdasarkan id. */
    suspend fun getConversation(conversationId: Long): Conversation?

    /**
     * Buka atau ambil kembali percakapan antara [userName] dan ahli gizi [nutritionistId].
     * Jika sudah ada, kembalikan yang lama. Jika belum, buat baru.
     */
    suspend fun startOrGetConversation(
        nutritionistId: String,
        nutritionistName: String,
        nutritionistSpecialty: String,
        userName: String
    ): Conversation

    /** Kirim pesan baru ke dalam percakapan [conversationId]. */
    suspend fun sendMessage(
        conversationId: Long,
        senderRole: com.example.nutriscan.domain.model.UserRole,
        content: String
    ): ChatMessage
}