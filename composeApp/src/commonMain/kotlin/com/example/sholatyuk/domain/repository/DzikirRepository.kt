// ── DzikirRepository.kt ──────────────────────────────────
package com.example.sholatyuk.domain.repository

import com.example.sholatyuk.domain.model.Dzikir
import com.example.sholatyuk.domain.model.DzikirCategory
import kotlinx.coroutines.flow.Flow

interface DzikirRepository {
    fun getAllDzikir(): Flow<List<Dzikir>>
    fun getDzikirByCategory(category: DzikirCategory): Flow<List<Dzikir>>
    suspend fun incrementTasbih(dzikirId: Long)
    suspend fun resetTasbih(dzikirId: Long)
    suspend fun seedInitialData()
}
