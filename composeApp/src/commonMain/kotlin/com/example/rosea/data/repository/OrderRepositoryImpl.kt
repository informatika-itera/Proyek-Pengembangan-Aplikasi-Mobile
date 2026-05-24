package com.example.rosea.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.domain.model.PendingOrder
import com.example.rosea.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(db: NoteDatabase) : OrderRepository {
    private val queries = db.orderQueries

    override fun getPendingOrdersFlow(): Flow<List<PendingOrder>> {
        return queries.getPendingOrders().asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { entity ->
                PendingOrder(entity.id, entity.total_price, entity.items_summary, entity.created_at, entity.status)
            }
        }
    }

    override suspend fun saveOrderLocally(totalPrice: Double, itemsSummary: String) {
        withContext(Dispatchers.IO) {
            queries.insertPendingOrder(
                total_price = totalPrice,
                items_summary = itemsSummary,
                created_at = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun syncOrderToApi(order: PendingOrder): Boolean {
        return try {
            // SIMULASI API POST REQUEST (Ktor)
            // Di dunia nyata, Anda mengirim data ini menggunakan client.post("https://api...")
            delay(2000) // Pura-pura sedang loading jaringan selama 2 detik

            // Anggap berhasil terkirim ke server, ubah status di SQLite menjadi SYNCED
            withContext(Dispatchers.IO) {
                queries.markOrderAsSynced(order.id)
            }
            true // Sukses
        } catch (e: Exception) {
            false // Gagal karena offline
        }
    }
}