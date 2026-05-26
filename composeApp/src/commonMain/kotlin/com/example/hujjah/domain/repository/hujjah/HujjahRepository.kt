package com.example.hujjah.domain.repository.hujjah

import com.example.hujjah.domain.model.islamic.IslamicReference
import com.example.hujjah.domain.model.islamic.TopicOption
import com.example.hujjah.domain.model.islamic.SurahItem
import com.example.hujjah.domain.model.islamic.VerseItem
import com.example.hujjah.domain.model.islamic.HadithBookItem
import com.example.hujjah.domain.model.islamic.HadithItem
import com.example.hujjah.domain.model.islamic.ChatMessage
import kotlinx.coroutines.flow.Flow

interface HujjahRepository {
    fun getTopics(): Flow<List<TopicOption>>
    fun getReferencesByTopic(topicId: String): Flow<List<IslamicReference>>
    fun getReferenceById(referenceId: String): Flow<IslamicReference?>

    // Quran
    fun getSurahs(forceRefresh: Boolean): Flow<List<SurahItem>>
    fun getSurahDetail(surahNumber: Int, surahName: String, forceRefresh: Boolean): Flow<List<VerseItem>>

    // Hadith
    fun getHadithBooks(forceRefresh: Boolean): Flow<List<HadithBookItem>>
    fun getHadithRange(bookId: String, start: Int, end: Int, forceRefresh: Boolean): Flow<List<HadithItem>>

    // Lens Chat Caching
    fun getChatHistory(): Flow<List<ChatMessage>>
    suspend fun saveChatMessage(message: ChatMessage)
    suspend fun deleteChatMessage(messageId: String)
    suspend fun clearChatHistory()
}
