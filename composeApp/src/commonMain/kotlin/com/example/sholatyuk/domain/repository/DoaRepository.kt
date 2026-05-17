// ── DoaRepository.kt ─────────────────────────────────────
package com.example.sholatyuk.domain.repository

import com.example.sholatyuk.domain.model.Doa
import com.example.sholatyuk.domain.model.DoaCategory
import kotlinx.coroutines.flow.Flow

interface DoaRepository {
    fun getAllDoa(): Flow<List<Doa>>
    fun getDoaByCategory(category: DoaCategory): Flow<List<Doa>>
    fun getFavoriteDoa(): Flow<List<Doa>>
    fun getDoaById(id: Long): Flow<Doa?>
    suspend fun toggleFavorite(id: Long)
    suspend fun seedInitialData()
}
