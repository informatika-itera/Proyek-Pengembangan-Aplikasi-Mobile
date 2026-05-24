package com.example.neurodeck.domain.repository

import com.example.neurodeck.domain.model.Deck
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak untuk operasi CRUD deck.
 *
 * Read operations return Flow → UI auto-update saat data berubah.
 * Write operations suspend → one-shot operations.
 */
interface DeckRepository {

    /** Stream semua deck, sorted by updatedAt DESC. Untuk Deck Library screen. */
    fun observeAllDecks(): Flow<List<Deck>>

    /** Stream 1 deck (nullable kalau dihapus). Untuk Deck Detail screen. */
    fun observeDeckById(id: Long): Flow<Deck?>

    /**
     * Insert deck baru. Return ID-nya untuk navigation ke deck baru.
     * Validasi (title non-empty) dilakukan di ViewModel sebelum panggil ini.
     */
    suspend fun createDeck(title: String, description: String): Long

    /** Update title/description deck existing. */
    suspend fun updateDeck(deck: Deck)

    /** Hapus deck + semua kartu di dalamnya (cascade by DB constraint). */
    suspend fun deleteDeck(id: Long)
}