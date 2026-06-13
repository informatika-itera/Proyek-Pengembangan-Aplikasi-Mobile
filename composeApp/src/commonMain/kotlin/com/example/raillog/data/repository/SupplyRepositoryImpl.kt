package com.example.raillog.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.raillog.data.local.RailLogDatabase
import com.example.raillog.domain.model.DraftItem
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.PartCategory
import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.model.SupplyStatus
import com.example.raillog.domain.repository.NotificationService
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SupplyRepositoryImpl(
    db: RailLogDatabase,
    private val notificationService: NotificationService
) : SupplyRepository {

    private val queries = db.supplyItemQueries
    private val draftQueries = db.draftRequisitionQueries

    // Implementasi migrasi
    override suspend fun migrateDataToUser(username: String) {
        withContext(Dispatchers.IO) {
            println("DEBUG_MIGRATION: Attempting migration for user: $username")
            queries.migrateDataToUser(username)
            println("DEBUG_MIGRATION: Migration command executed for: $username")
        }
    }

    override fun getAllItems(activeUsername: String): Flow<List<SupplyItem>> {
        println("DEBUG_QUERY: Fetching items for activeUsername: '$activeUsername'")
        return queries.getAllItems(activeUsername)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                println("DEBUG_QUERY: Found ${entities.size} items for user: '$activeUsername' in DB")
                entities.map { entity ->
                    SupplyItem(
                        id = entity.id,
                        partCode = entity.part_code,
                        name = entity.name,
                        category = PartCategory.fromString(entity.category),
                        quantity = entity.quantity.toInt(),
                        unit = entity.unit,
                        supplier = entity.supplier,
                        status = SupplyStatus.fromString(entity.status),
                        priority = Priority.fromString(entity.priority),
                        documentRef = entity.document_ref,
                        notes = entity.notes,
                        createdAt = Instant.fromEpochMilliseconds(entity.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(entity.updated_at),
                        createdBy = entity.created_by
                    )
                }
            }
    }

    override fun getItemById(id: Long): Flow<SupplyItem?> {
        return queries.getItemById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    SupplyItem(
                        id = it.id,
                        partCode = it.part_code,
                        name = it.name,
                        category = PartCategory.fromString(it.category),
                        quantity = it.quantity.toInt(),
                        unit = it.unit,
                        supplier = it.supplier,
                        status = SupplyStatus.fromString(it.status),
                        priority = Priority.fromString(it.priority),
                        documentRef = it.document_ref,
                        notes = it.notes,
                        createdAt = Instant.fromEpochMilliseconds(it.created_at),
                        updatedAt = Instant.fromEpochMilliseconds(it.updated_at),
                        createdBy = it.created_by
                    )
                }
            }
    }

    override suspend fun insertItem(item: SupplyItem, activeUsername: String) {
        withContext(Dispatchers.IO) {
            val now = Clock.System.now().toEpochMilliseconds()
            queries.insertItem(
                part_code = item.partCode,
                name = item.name,
                category = item.category.name,
                quantity = item.quantity.toLong(),
                unit = item.unit,
                supplier = item.supplier,
                status = item.status.name,
                priority = item.priority.name,
                document_ref = item.documentRef,
                notes = item.notes,
                created_at = now,
                updated_at = now,
                created_by = activeUsername
            )
            if (item.priority == Priority.CRITICAL || item.priority == Priority.HIGH) {
                notificationService.showCriticalAlert(item.name, item.quantity)
            }
        }
    }

    override suspend fun updateItem(item: SupplyItem) {
        withContext(Dispatchers.IO) {
            queries.updateItem(
                part_code = item.partCode,
                name = item.name,
                category = item.category.name,
                quantity = item.quantity.toLong(),
                unit = item.unit,
                supplier = item.supplier,
                status = item.status.name,
                priority = item.priority.name,
                document_ref = item.documentRef,
                notes = item.notes,
                updated_at = Clock.System.now().toEpochMilliseconds(),
                id = item.id
            )
            if (item.priority == Priority.CRITICAL || item.priority == Priority.HIGH) {
                notificationService.showCriticalAlert(item.name, item.quantity)
            }
        }
    }

    override suspend fun deleteItem(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteItem(id)
        }
    }

    override suspend fun updateStatus(id: Long, status: SupplyStatus) {
        withContext(Dispatchers.IO) {
            queries.updateStatus(
                status = status.name,
                updated_at = Clock.System.now().toEpochMilliseconds(),
                id = id
            )
        }
    }

    override suspend fun saveDraft(draftId: String, projectTitle: String, currentStep: Int, lastUpdated: Long, formStateJson: String, activeUsername: String) {
        withContext(Dispatchers.IO) {
            draftQueries.insertOrReplaceDraft(draftId, projectTitle, currentStep.toLong(), lastUpdated, formStateJson, activeUsername)
        }
    }

    override fun getAllDrafts(activeUsername: String): Flow<List<DraftItem>> {
        return draftQueries.getAllDrafts(activeUsername).asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { DraftItem(it.draftId, it.projectTitle, it.currentStep.toInt(), it.lastUpdated, it.formStateJson) }
        }
    }

    override suspend fun deleteDraft(draftId: String) {
        withContext(Dispatchers.IO) { draftQueries.deleteDraftById(draftId) }
    }
}
