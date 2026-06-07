package id.pusakakata.domain.repository

import id.pusakakata.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllWords(): Flow<List<Word>>
    fun getFavoriteWords(): Flow<List<Word>>
    suspend fun getWordById(id: String): Word?
    suspend fun insertWord(word: Word)
    suspend fun updateWord(word: Word)
    suspend fun updateSrs(wordId: String, quality: Int)
    suspend fun toggleFavorite(wordId: String)
    suspend fun deleteWord(id: String)
    suspend fun searchAndSave(word: String): Result<Word>
    suspend fun getAiDefinition(word: String): Result<String>
    
    // Token Logic
    fun getTokens(): Flow<Long>
    suspend fun addTokens(amount: Long)
    suspend fun useToken(): Boolean
    
    // Quiz logic
    suspend fun getRandomWords(limit: Long): List<Word>
    
    // Collection logic
    fun getCollectedCardIds(): Flow<List<String>>
    suspend fun saveCollectedCard(cardId: String)
}
