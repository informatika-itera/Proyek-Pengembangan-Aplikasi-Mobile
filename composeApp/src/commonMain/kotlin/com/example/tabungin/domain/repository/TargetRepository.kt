package com.example.tabungin.domain.repository

import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import kotlinx.coroutines.flow.Flow

interface TargetRepository {
    fun getAllTargets(): Flow<List<Target>>
    fun getTargetById(id: Long): Flow<Target?>
    suspend fun insertTarget(target: Target): Long
    suspend fun updateTarget(target: Target)
    suspend fun deleteTarget(id: Long)


    fun getSetoranByTarget(targetId: Long): Flow<List<Setoran>>
    fun getAllSetoran(): Flow<List<Setoran>>
    suspend fun insertSetoran(setoran: Setoran)
    suspend fun deleteSetoran(id: Long)
}
