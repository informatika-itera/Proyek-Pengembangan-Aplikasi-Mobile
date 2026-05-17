package com.example.tabungin.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.tabungin.data.local.TabunginDatabase
import com.example.tabungin.data.local.entity.toDomain
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.repository.TargetRepository
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock


class TargetRepositoryImpl(
    private val db: TabunginDatabase
) : TargetRepository {

    private val targetQueries   = db.targetQueries
    private val setoranQueries  = db.setoranQueries

    private fun now(): String =
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString()



    override fun getAllTargets(): Flow<List<Target>> =
        targetQueries.getAllTargets()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override fun getTargetById(id: Long): Flow<Target?> =
        targetQueries.getTargetById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }

    override suspend fun insertTarget(target: Target): Long =
        withContext(Dispatchers.IO) {
            targetQueries.insertTarget(
                nama         = target.nama,
                targetAmount = target.targetAmount,
                deadline     = target.deadline,
                icon         = target.icon,
                warna        = target.warna,
                createdAt    = now(),
                updatedAt    = now()
            )
            targetQueries.lastInsertRowId().executeAsOne()
        }

    override suspend fun updateTarget(target: Target) =
        withContext(Dispatchers.IO) {
            targetQueries.updateTarget(
                nama         = target.nama,
                targetAmount = target.targetAmount,
                deadline     = target.deadline,
                icon         = target.icon,
                warna        = target.warna,
                updatedAt    = now(),
                id           = target.id
            )
        }

    override suspend fun deleteTarget(id: Long) =
        withContext(Dispatchers.IO) {
            targetQueries.deleteTarget(id)
        }



    override fun getSetoranByTarget(targetId: Long): Flow<List<Setoran>> =
        setoranQueries.getSetoranByTarget(targetId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override fun getAllSetoran(): Flow<List<Setoran>> =
        setoranQueries.getAllSetoran()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun insertSetoran(setoran: Setoran) =
        withContext(Dispatchers.IO) {
            setoranQueries.insertSetoran(
                targetId  = setoran.targetId,
                amount    = setoran.amount,
                catatan   = setoran.catatan,
                tanggal   = setoran.tanggal,
                createdAt = now()
            )
        }

    override suspend fun deleteSetoran(id: Long) =
        withContext(Dispatchers.IO) {
            setoranQueries.deleteSetoran(id)
        }
}
