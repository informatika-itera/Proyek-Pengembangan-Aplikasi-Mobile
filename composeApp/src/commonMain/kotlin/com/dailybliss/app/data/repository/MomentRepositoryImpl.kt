package com.dailybliss.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.dailybliss.app.data.local.BlissDatabase
import com.dailybliss.app.data.local.entity.toDomain
import com.dailybliss.app.data.local.entity.toDomainList
import com.dailybliss.app.data.local.entity.toEntityValues
import com.dailybliss.app.domain.model.Moment
import com.dailybliss.app.domain.repository.MomentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class MomentRepositoryImpl(
    database: BlissDatabase
) : MomentRepository {
    private val queries = database.momentQueries

    override fun getAllMoments(): Flow<List<Moment>> =
        queries.getAllMoments().asFlow().mapToList(Dispatchers.Default).map { it.toDomainList() }

    override fun getPinnedMoments(): Flow<List<Moment>> =
        queries.getPinnedMoments().asFlow().mapToList(Dispatchers.Default).map { it.toDomainList() }

    override fun searchMoments(query: String): Flow<List<Moment>> =
        queries.searchMoments(query, query).asFlow().mapToList(Dispatchers.Default).map { it.toDomainList() }

    override fun getMomentById(id: Long): Flow<Moment?> =
        queries.getMomentById(id).asFlow().mapToOneOrNull(Dispatchers.Default).map { it?.toDomain() }

    override suspend fun insertMoment(moment: Moment): Long = withContext(Dispatchers.Default) {
        val values = moment.toEntityValues()
        queries.insertMoment(
            title = values.title,
            content = values.content,
            media_url = values.mediaUrl,
            is_pinned = values.isPinned,
            created_at = values.createdAt,
            updated_at = values.updatedAt
        )
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun updateMoment(moment: Moment) = withContext(Dispatchers.Default) {
        val values = moment.toEntityValues()
        queries.updateMoment(
            title = values.title,
            content = values.content,
            media_url = values.mediaUrl,
            is_pinned = values.isPinned,
            updated_at = Clock.System.now().toEpochMilliseconds(),
            id = moment.id
        )
    }

    override suspend fun deleteMoment(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteMoment(id)
    }

    override suspend fun deleteMoments(ids: List<Long>) = withContext(Dispatchers.Default) {
        queries.deleteMomentsByIds(ids)
    }
}

