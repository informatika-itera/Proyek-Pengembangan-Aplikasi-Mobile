package com.kosthub.app.domain.repository

import com.kosthub.app.domain.model.Kost
import kotlinx.coroutines.flow.Flow

interface KostRepository {
    suspend fun getAll(): List<Kost>
    fun getAllFlow(): Flow<List<Kost>>
    suspend fun syncRemote()
    suspend fun getById(id: Long): Kost?
    suspend fun update(kost: Kost)
}

