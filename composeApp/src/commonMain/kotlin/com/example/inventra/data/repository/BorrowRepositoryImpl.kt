package com.example.inventra.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.inventra.core.network.SupabaseClientProvider
import com.example.inventra.data.local.InventRaDatabase
import com.example.inventra.data.local.entity.toDomain
import com.example.inventra.data.remote.dto.BorrowRecordDto
import com.example.inventra.data.remote.dto.InsertBorrowDto
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.domain.repository.BorrowRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class BorrowRepositoryImpl(
    private val database: InventRaDatabase
) : BorrowRepository {

    private val client = SupabaseClientProvider.client
    private val db = client.postgrest
    private val auth = client.auth
    private val queries = database.borrowRecordQueries
    private val finePerDay = 10_000L
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getAllRecords(): Flow<List<BorrowRecord>> {
        syncScope.launch { syncRecordsFromSupabase() }
        return queries.getAllRecords()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override fun getActiveRecords(): Flow<List<BorrowRecord>> {
        syncScope.launch { syncRecordsFromSupabase() }
        return queries.getActiveRecords()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override suspend fun borrowItem(record: BorrowRecord): Long {
        // Simpan ke SQLDelight dengan status PENDING
        queries.insertRecord(
            item_id = record.itemId,
            item_name = record.itemName,
            borrower_name = record.borrowerName,
            borrow_date = record.borrowDate.toEpochMilliseconds(),
            due_date = record.dueDate.toEpochMilliseconds(),
            return_date = null,
            status = BorrowStatus.PENDING.name,
            fine_amount = 0L
        )
        val localId = queries.lastInsertId().executeAsOne()

        // Kurangi available_stock di SQLDelight lokal
        val itemQueries = database.itemQueries
        val existingItem = itemQueries.getItemById(record.itemId).executeAsOneOrNull()
        if (existingItem != null && existingItem.available_stock > 0) {
            itemQueries.updateItem(
                name = existingItem.name,
                description = existingItem.description,
                category = existingItem.category,
                location = existingItem.location,
                total_stock = existingItem.total_stock,
                available_stock = existingItem.available_stock - 1,
                condition = existingItem.condition,
                pic_name = existingItem.pic_name,
                image_url = existingItem.image_url,
                updated_at = Clock.System.now().toEpochMilliseconds(),
                id = record.itemId
            )
        }

        // Sync ke Supabase di background
        syncScope.launch {
            try {
                val userId = auth.currentUserOrNull()?.id ?: "anonymous"
                val dto = InsertBorrowDto(
                    itemId = record.itemId.toString(),
                    borrowerId = userId,
                    itemName = record.itemName,
                    borrowerName = record.borrowerName,
                    division = "HMIF",
                    quantity = 1,
                    dueDate = record.dueDate.toString(),
                    status = BorrowStatus.PENDING.name
                )
                db["borrow_records"].insert(dto)
                // Update available_stock di Supabase
                val newStock = (existingItem?.available_stock?.minus(1) ?: 0).coerceAtLeast(0)
                db["items"].update(
                    mapOf("available_stock" to newStock)
                ) { filter { eq("id", record.itemId.toString()) } }
            } catch (e: Exception) {
                println("Borrow sync gagal: ${e.message}")
            }
        }
        return localId
    }

    override suspend fun returnItem(recordId: Long) {
        val returnDate = Clock.System.now()
        val record = queries.getAllRecords().executeAsList()
            .find { it.id == recordId } ?: return

        val dueDate = Instant.fromEpochMilliseconds(record.due_date)
        val fineAmount = if (returnDate > dueDate) {
            val diffMs = returnDate.toEpochMilliseconds() - dueDate.toEpochMilliseconds()
            val diffDays = (diffMs / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
            diffDays * finePerDay
        } else 0L

        queries.updateRecordStatus(
            status = BorrowStatus.RETURNED.name,
            return_date = returnDate.toEpochMilliseconds(),
            fine_amount = fineAmount,
            id = recordId
        )

        // Kembalikan available_stock
        val itemQueries = database.itemQueries
        val existingItem = itemQueries.getItemById(record.item_id).executeAsOneOrNull()
        if (existingItem != null) {
            val newAvailable = (existingItem.available_stock + 1)
                .coerceAtMost(existingItem.total_stock)
            itemQueries.updateItem(
                name = existingItem.name,
                description = existingItem.description,
                category = existingItem.category,
                location = existingItem.location,
                total_stock = existingItem.total_stock,
                available_stock = newAvailable,
                condition = existingItem.condition,
                pic_name = existingItem.pic_name,
                image_url = existingItem.image_url,
                updated_at = Clock.System.now().toEpochMilliseconds(),
                id = record.item_id
            )
            syncScope.launch {
                try {
                    db["items"].update(
                        mapOf("available_stock" to newAvailable)
                    ) { filter { eq("id", record.item_id.toString()) } }
                    db["borrow_records"].update(
                        mapOf(
                            "status" to BorrowStatus.RETURNED.name,
                            "fine_amount" to fineAmount,
                            "return_date" to returnDate.toString()
                        )
                    ) { filter { eq("id", recordId.toString()) } }
                } catch (e: Exception) {
                    println("Return sync gagal: ${e.message}")
                }
            }
        }
    }

    override suspend fun approveRequest(recordId: Long) {
        queries.updateRecordStatus(
            status = BorrowStatus.ACTIVE.name,
            return_date = null,
            fine_amount = 0L,
            id = recordId
        )
        syncScope.launch {
            try {
                db["borrow_records"].update(
                    mapOf("status" to BorrowStatus.ACTIVE.name)
                ) { filter { eq("id", recordId.toString()) } }
            } catch (e: Exception) {
                println("Approve sync gagal: ${e.message}")
            }
        }
    }

    private suspend fun syncRecordsFromSupabase() {
        try {
            val records = db["borrow_records"]
                .select { order("created_at", Order.DESCENDING) }
                .decodeList<BorrowRecordDto>()
            database.transaction {
                queries.deleteAll()
                records.forEach { dto ->
                    queries.insertRecord(
                        item_id = 0L,
                        item_name = dto.itemName,
                        borrower_name = dto.borrowerName,
                        borrow_date = runCatching {
                            Instant.parse(dto.borrowDate).toEpochMilliseconds()
                        }.getOrDefault(Clock.System.now().toEpochMilliseconds()),
                        due_date = runCatching {
                            Instant.parse(dto.dueDate).toEpochMilliseconds()
                        }.getOrDefault(Clock.System.now().toEpochMilliseconds()),
                        return_date = dto.returnDate?.let {
                            runCatching { Instant.parse(it).toEpochMilliseconds() }.getOrNull()
                        },
                        status = dto.status,
                        fine_amount = dto.fineAmount
                    )
                }
            }
            println("SYNC: ${records.size} borrow records synced")
        } catch (e: Exception) {
            println("SYNC borrow offline: ${e.message}")
        }
    }
}