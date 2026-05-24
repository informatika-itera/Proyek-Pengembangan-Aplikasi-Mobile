package com.example.neurodeck.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.local.mapper.toDomain
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.repository.DeckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * SQLDelight-backed implementation of [DeckRepository].
 *
 * Catatan implementasi:
 * - Read operations return Flow → UI auto-update saat tabel berubah.
 * - Write operations suspend → dispatch ke IO supaya tidak block main thread.
 * - Timestamps dihitung di sini (bukan di ViewModel) supaya source-of-truth
 *   tunggal. Domain layer tidak tahu konsep epoch millis — repository yang convert.
 */
class DeckRepositoryImpl(
    database: NeuroDeckDatabase,
) : DeckRepository {

    private val queries = database.deckQueries

    override fun observeAllDecks(): Flow<List<Deck>> =
        queries.selectAllWithCardCount()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeDeckById(id: Long): Flow<Deck?> =
        queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }

    override suspend fun createDeck(title: String, description: String): Long =
        withContext(Dispatchers.IO) {
            val now = Clock.System.now().toEpochMilliseconds()
            queries.transactionWithResult {
                queries.insert(
                    title = title,
                    description = description,
                    createdAt = now,
                    updatedAt = now,
                )
                queries.lastInsertedId().executeAsOne()
            }
        }

    override suspend fun updateDeck(deck: Deck) {
        withContext(Dispatchers.IO) {
            queries.update(
                title = deck.title,
                description = deck.description,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                id = deck.id,
            )
        }
    }

    override suspend fun deleteDeck(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteById(id)
        }
    }
}