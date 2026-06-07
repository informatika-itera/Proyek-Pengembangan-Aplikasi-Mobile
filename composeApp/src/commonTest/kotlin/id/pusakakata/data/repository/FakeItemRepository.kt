package id.pusakakata.data.repository

import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeItemRepository : ItemRepository {
    private val _words = MutableStateFlow<List<Word>>(emptyList())
    private val _tokens = MutableStateFlow(0L)

    override fun getAllWords(): Flow<List<Word>> = _words.asStateFlow()

    override fun getFavoriteWords(): Flow<List<Word>> = _words.map { words ->
        words.filter { it.isFavorite }
    }

    override suspend fun getWordById(id: String): Word? = _words.value.find { it.id == id }

    override suspend fun insertWord(word: Word) {
        _words.value = _words.value + word
    }

    override suspend fun updateWord(word: Word) {
        _words.value = _words.value.map { if (it.id == word.id) word else it }
    }

    override suspend fun updateSrs(wordId: String, quality: Int) {
        val word = getWordById(wordId) ?: return
        // Minimal logic for test
        updateWord(word.copy(srsData = word.srsData.copy(level = word.srsData.level + 1)))
    }

    override suspend fun toggleFavorite(wordId: String) {
        val word = getWordById(wordId) ?: return
        updateWord(word.copy(isFavorite = !word.isFavorite))
    }

    override suspend fun deleteWord(id: String) {
        _words.value = _words.value.filter { it.id != id }
    }

    override suspend fun searchAndSave(word: String): Result<Word> {
        val newWord = Word(id = "new_id", term = word, definition = "AI Def", category = "AI")
        insertWord(newWord)
        return Result.success(newWord)
    }

    override suspend fun getAiDefinition(word: String): Result<String> {
        return Result.success("AI Definition for $word")
    }

    override fun getTokens(): Flow<Long> = _tokens.asStateFlow()

    override suspend fun addTokens(amount: Long) {
        _tokens.value += amount
    }

    override suspend fun useToken(): Boolean {
        if (_tokens.value > 0) {
            _tokens.value -= 1
            return true
        }
        return false
    }

    override suspend fun getRandomWords(limit: Long): List<Word> {
        return _words.value.shuffled().take(limit.toInt())
    }
}
