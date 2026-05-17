package com.example.bridgebit.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.bridgebit.data.local.BridgeBitDatabase
import com.example.bridgebit.data.local.entity.toDomain
import com.example.bridgebit.data.local.entity.toDomainList
import com.example.bridgebit.data.local.entity.toEntityValues
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.TranslationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class TranslationRepositoryImpl(private val database: BridgeBitDatabase) : TranslationRepository {

    // Objek queries di-generate otomatis dari BridgeBit.sq
    private val queries = database.bridgeBitQueries

    override fun getAllHistory(): Flow<List<Translation>> {
        return queries.getAllHistory()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }

    override fun getVaultPhrases(): Flow<List<Translation>> {
        return queries.getVaultPhrases()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }

    override fun searchHistory(query: String): Flow<List<Translation>> {
        return queries.searchHistory(query, query)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }

    override fun getTranslationById(id: Long): Flow<Translation?> {
        return queries.getTranslationById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun insertTranslation(translation: Translation): Long = withContext(Dispatchers.Default) {
        val values = translation.toEntityValues()
        queries.insertTranslation(
            source_text = values.sourceText,
            translated_text = values.translatedText,
            source_language = values.sourceLanguage,
            target_language = values.targetLanguage,
            is_vaulted = if (values.isVaulted) 1L else 0L,
            created_at = values.createdAt,
            updated_at = values.updatedAt
        )
        // Mengembalikan ID dari baris yang baru saja dimasukkan
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun updateTranslation(translation: Translation) = withContext(Dispatchers.Default) {
        val values = translation.toEntityValues()
        queries.updateTranslation(
            id = translation.id,
            source_text = values.sourceText,
            translated_text = values.translatedText,
            source_language = values.sourceLanguage,
            target_language = values.targetLanguage,
            is_vaulted = if (values.isVaulted) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteTranslationById(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteTranslationById(id)
    }

    override suspend fun toggleVaultStatus(id: Long) = withContext(Dispatchers.Default) {
        queries.toggleVaultStatus(
            id = id,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteTranslationsByIds(ids: List<Long>) = withContext(Dispatchers.Default) {
        queries.deleteTranslationsByIds(ids)
    }
}