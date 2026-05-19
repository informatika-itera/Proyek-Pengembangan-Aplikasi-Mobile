package com.kosthub.app.domain.repository

import com.kosthub.app.domain.model.Kost

interface KostRepository {
    suspend fun getAll(): List<Kost>
    suspend fun getById(id: Long): Kost?
    suspend fun add(kost: Kost): Long
    suspend fun update(kost: Kost)
    suspend fun delete(id: Long)
    suspend fun seedIfEmpty(items: List<Kost>)
}
