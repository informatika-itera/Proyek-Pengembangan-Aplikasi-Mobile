package com.example.bridgebit.domain.repository

import com.example.bridgebit.domain.model.Translation
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    fun getAllHistory(): Flow<List<Translation>>
    fun getVaultPhrases(): Flow<List<Translation>>
    fun searchHistory(query: String): Flow<List<Translation>>
    fun getTranslationById(id: Long): Flow<Translation?>

    suspend fun insertTranslation(translation: Translation): Long
    suspend fun updateTranslation(translation: Translation)
    suspend fun deleteTranslationById(id: Long)
    suspend fun deleteTranslationsByIds(ids: List<Long>)
    suspend fun toggleVaultStatus(id: Long)
}