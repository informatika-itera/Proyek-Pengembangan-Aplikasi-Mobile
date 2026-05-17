package id.pusakakata.data.repository

import id.pusakakata.data.local.PusakaDatabase
import id.pusakakata.domain.model.SRSData
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class WordRepositoryImpl(db: PusakaDatabase) : WordRepository {
    private val queries = db.pusakaDatabaseQueries

    override fun getAllWords(): Flow<List<Word>> {
        return queries.getAllWords().asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getWordById(id: String): Word? {
        return queries.getWordById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insertWord(word: Word) {
        queries.insertWord(
            id = word.id,
            term = word.term,
            definition = word.definition,
            category = word.category,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            intervalDays = word.srsData.intervalDays.toLong(),
            easeFactor = word.srsData.easeFactor,
            nextReview = word.srsData.nextReview?.toEpochMilliseconds(),
            level = word.srsData.level.toLong()
        )
    }

    override suspend fun updateWord(word: Word) {
        queries.updateWord(
            term = word.term,
            definition = word.definition,
            category = word.category,
            intervalDays = word.srsData.intervalDays.toLong(),
            easeFactor = word.srsData.easeFactor,
            nextReview = word.srsData.nextReview?.toEpochMilliseconds(),
            level = word.srsData.level.toLong(),
            id = word.id
        )
    }

    override suspend fun deleteWord(id: String) {
        queries.deleteWord(id)
    }

    private fun id.pusakakata.data.local.WordEntity.toDomain() = Word(
        id = id,
        term = term,
        definition = definition,
        category = category,
        srsData = SRSData(
            intervalDays = intervalDays.toInt(),
            easeFactor = easeFactor,
            nextReview = nextReview?.let { Instant.fromEpochMilliseconds(it) },
            level = level.toInt()
        )
    )
}
