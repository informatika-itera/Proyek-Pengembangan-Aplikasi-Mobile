package com.example.rosea.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.data.local.PendingOrderEntity
import com.example.rosea.domain.model.PendingOrder
import com.example.rosea.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(db: NoteDatabase) : OrderRepository {
    private val queries = db.orderQueries

    override fun getPendingOrdersFlow(): Flow<List<PendingOrder>> {
        return queries.getPendingOrders()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities: List<PendingOrderEntity> ->
                entities.map { entity ->
                    PendingOrder(
                        id = entity.id,
                        totalPrice = entity.total_price,
                        itemsSummary = entity.items_summary,
                        createdAt = entity.created_at,
                        status = entity.status
                    )
                }
            }
    }

    override suspend fun saveOrderLocally(totalPrice: Double, itemsSummary: String) {
        withContext(Dispatchers.Default) {
            queries.insertPendingOrder(
                total_price = totalPrice,
                items_summary = itemsSummary,
                created_at = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun syncOrderToApi(order: PendingOrder): Boolean {
        return try {
            delay(2000)
            withContext(Dispatchers.Default) {
                queries.markOrderAsSynced(order.id)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
