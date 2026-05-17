package com.dailybliss.app.domain.repository

import com.dailybliss.app.domain.model.Moment
import kotlinx.coroutines.flow.Flow

interface MomentRepository {
    fun getAllMoments(): Flow<List<Moment>>
    fun getPinnedMoments(): Flow<List<Moment>>
    fun searchMoments(query: String): Flow<List<Moment>>
    fun getMomentById(id: Long): Flow<Moment?>
    suspend fun insertMoment(moment: Moment): Long
    suspend fun updateMoment(moment: Moment)
    suspend fun deleteMoment(id: Long)
    suspend fun deleteMoments(ids: List<Long>)
}

