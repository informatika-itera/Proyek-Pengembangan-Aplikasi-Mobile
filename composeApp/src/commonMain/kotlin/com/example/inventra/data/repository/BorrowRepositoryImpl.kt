package com.example.inventra.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.inventra.core.network.SupabaseClientProvider
import com.example.inventra.data.local.InventRaDatabase
import com.example.inventra.data.local.entity.toDomain
import com.example.inventra.data.remote.dto.BorrowRecordDto
import com.example.inventra.data.remote.dto.InsertBorrowDto
import com.example.inventra.data.remote.dto.ItemDto
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class BorrowRepositoryImpl(
    private val database: InventRaDatabase
) : BorrowRepository {

    private val client = SupabaseClientProvider.client
    private val adminClient = SupabaseClientProvider.adminClient // Tambahkan ini
    private val db = client.postgrest
    private val adminDb = adminClient.postgrest // Tambahkan ini
    private val auth = client.auth
    private val queries = database.borrowRecordQueries
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var lastSyncTime = 0L
    private val SYNC_COOLDOWN_MS = 60_000L

    override fun getAllRecords(): Flow<List<BorrowRecord>> {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastSyncTime > SYNC_COOLDOWN_MS) {
            syncScope.launch {
                syncRecordsFromSupabase()
                lastSyncTime = Clock.System.now().toEpochMilliseconds()
            }
        }
        return queries.getAllRecords()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override fun getActiveRecords(): Flow<List<BorrowRecord>> {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastSyncTime > SYNC_COOLDOWN_MS) {
            syncScope.launch {
                syncRecordsFromSupabase()
                lastSyncTime = Clock.System.now().toEpochMilliseconds()
            }
        }
        return queries.getActiveRecords()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override suspend fun borrowItem(record: BorrowRecord): Long {
        // 1. Insert borrow record lokal
        queries.insertRecord(
            remote_id = null,
            item_id = record.itemId,
            borrower_id = record.borrowerId,
            item_name = record.itemName,
            borrower_name = record.borrowerName,
            borrower_division = record.borrowerDivision,
            borrow_date = record.borrowDate.toEpochMilliseconds(),
            due_date = record.dueDate.toEpochMilliseconds(),
            return_date = null,
            status = BorrowStatus.PENDING.name,
            fine_amount = 0L,
            return_proof_url = null
        )
        val localId = queries.lastInsertId().executeAsOne()

        // 2. Kurangi available_stock SEGERA (HANYA LOKAL)
        val now = Clock.System.now().toEpochMilliseconds()
        
        // Reset cooldown
        lastSyncTime = now

        val existingItem = database.itemQueries.getItemById(record.itemId).executeAsOneOrNull()
        try {
            if (existingItem != null && existingItem.available_stock > 0) {
                val newStock = existingItem.available_stock - 1
                database.itemQueries.updateAvailableStock(
                    available_stock = newStock,
                    updated_at = now,
                    id = record.itemId
                )
            }
        } catch (e: Exception) {
            // BORROW: update stok lokal gagal
        }

        // 3. Sync ke Supabase di background
        syncScope.launch {
            try {
                val currentUser = SupabaseClientProvider.client.auth.currentUserOrNull()
                val profile = currentUser?.let {
                    db["profiles"].select { filter { eq("id", it.id) } }.decodeSingleOrNull<com.example.inventra.data.remote.dto.ProfileDto>()
                }
                val division = profile?.division ?: record.borrowerDivision.ifBlank { "PUBDOK" }

                val remoteItemId = existingItem?.remote_id ?: record.itemId.toString()

                val dto = InsertBorrowDto(
                    itemId = remoteItemId,
                    borrowerId = currentUser?.id ?: "anonymous",
                    itemName = record.itemName,
                    borrowerName = record.borrowerName,
                    division = division,
                    quantity = 1,
                    dueDate = record.dueDate.toString(),
                    status = BorrowStatus.PENDING.name
                )
                val response = db["borrow_records"].insert(dto) { select() }.decodeSingle<BorrowRecordDto>()
                
                // Update remote_id di lokal
                queries.updateRemoteId(remote_id = response.id, id = localId)

                // Update available_stock di Supabase
                // Gunakan adminDb agar selalu berhasil meskipun dijalankan oleh role MEMBER
                val currentRemote = adminDb["items"]
                    .select { filter { eq("id", remoteItemId) } }
                    .decodeSingleOrNull<ItemDto>()
                if (currentRemote != null && currentRemote.availableStock > 0) {
                    val updateData = buildJsonObject {
                        put("available_stock", currentRemote.availableStock - 1)
                    }
                    adminDb["items"].update(updateData) { filter { eq("id", remoteItemId) } }
                }
                
                // Trigger refresh item agar dashboard semua orang sinkron
                lastSyncTime = 0
            } catch (e: Exception) {
                // BORROW Supabase sync gagal
            }
        }

        return localId
    }

    override suspend fun returnItem(recordId: Long, proofImageUrl: String?) {
        val returnDate = Clock.System.now()
        val record = queries.getRecordById(recordId).executeAsOneOrNull() ?: return

        val dueDate = Instant.fromEpochMilliseconds(record.due_date)
        val fineAmount = if (returnDate > dueDate) {
            val diffMs = returnDate.toEpochMilliseconds() - dueDate.toEpochMilliseconds()
            val diffDays = (diffMs / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
            diffDays * 10_000L
        } else 0L

        queries.updateRecordStatus(
            status = BorrowStatus.PENDING_RETURN.name,
            return_date = returnDate.toEpochMilliseconds(),
            fine_amount = fineAmount,
            return_proof_url = proofImageUrl,
            id = recordId
        )

        // Reset cooldown agar sync tidak menimpa perubahan lokal yang baru saja dibuat
        lastSyncTime = Clock.System.now().toEpochMilliseconds()

        // Catatan: Stok belum dikembalikan ke item sampai Admin Approve pengembalian ini.

        syncScope.launch {
            try {
                val targetId = record.remote_id ?: return@launch
                val updateData = buildJsonObject {
                    put("status", BorrowStatus.PENDING_RETURN.name)
                    put("fine_amount", fineAmount)
                    put("return_date", returnDate.toString())
                    if (proofImageUrl != null) put("return_proof_url", proofImageUrl)
                }
                adminDb["borrow_records"].update(updateData) { filter { eq("id", targetId) } }
                lastSyncTime = 0
            } catch (e: Exception) {
                // RETURN Supabase sync gagal
            }
        }
    }

    override suspend fun approveReturn(recordId: Long) {
        val record = queries.getRecordById(recordId).executeAsOneOrNull() ?: return
        
        // 1. Update status jadi RETURNED
        queries.updateStatus(status = BorrowStatus.RETURNED.name, id = recordId)
        
        // 2. Kembalikan stok lokal
        try {
            val existingItem = database.itemQueries.getItemById(record.item_id).executeAsOneOrNull()
            if (existingItem != null) {
                val newStock = (existingItem.available_stock + 1).coerceAtMost(existingItem.total_stock)
                database.itemQueries.updateAvailableStock(
                    available_stock = newStock,
                    updated_at = Clock.System.now().toEpochMilliseconds(),
                    id = record.item_id
                )
            }
        } catch (e: Exception) {
            // APPROVE_RETURN: update stok lokal gagal
        }

        lastSyncTime = Clock.System.now().toEpochMilliseconds()

        syncScope.launch {
            try {
                val targetId = record.remote_id ?: return@launch
                val updateData = buildJsonObject {
                    put("status", BorrowStatus.RETURNED.name)
                }
                adminDb["borrow_records"].update(updateData) { filter { eq("id", targetId) } }

                // Kembalikan stok di Supabase
                val existingItem = database.itemQueries.getItemById(record.item_id).executeAsOneOrNull()
                val remoteItemId = existingItem?.remote_id
                if (remoteItemId != null) {
                    val currentRemote = adminDb["items"]
                        .select { filter { eq("id", remoteItemId) } }
                        .decodeSingleOrNull<ItemDto>()
                    if (currentRemote != null) {
                        val newStock = (currentRemote.availableStock + 1).coerceAtMost(currentRemote.totalStock)
                        adminDb["items"].update(buildJsonObject {
                            put("available_stock", newStock)
                        }) { filter { eq("id", remoteItemId) } }
                    }
                }
                lastSyncTime = 0
            } catch (e: Exception) {
                // APPROVE_RETURN Supabase sync gagal
            }
        }
    }

    override suspend fun approveRequest(recordId: Long) {
        val record = queries.getRecordById(recordId).executeAsOneOrNull()
        queries.updateStatus(status = BorrowStatus.ACTIVE.name, id = recordId)
        syncScope.launch {
            try {
                val targetId = record?.remote_id ?: return@launch
                val updateData = buildJsonObject {
                    put("status", BorrowStatus.ACTIVE.name)
                }
                adminDb["borrow_records"].update(updateData) { filter { eq("id", targetId) } }
            } catch (e: Exception) {
                // APPROVE Supabase sync gagal
            }
        }
    }

    override suspend fun refresh() {
        syncRecordsFromSupabase()
        lastSyncTime = Clock.System.now().toEpochMilliseconds()
    }

    override suspend fun deleteAll() {
        queries.deleteAll()
        try {
            adminDb["borrow_records"].delete {
                filter { neq("id", "00000000-0000-0000-0000-000000000000") }
            }
        } catch (e: Exception) {
            // deleteAll Supabase borrow_records error
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
                    // Cari local item_id berdasarkan remote_id (UUID string dari Supabase)
                    val localItem = database.itemQueries.getItemByRemoteId(dto.itemId).executeAsOneOrNull()
                    
                    queries.insertRecord(
                        remote_id = dto.id,
                        item_id = localItem?.id ?: 0L,
                        borrower_id = dto.borrowerId,
                        item_name = dto.itemName,
                        borrower_name = dto.borrowerName,
                        borrower_division = dto.division,
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
                        fine_amount = dto.fineAmount,
                        return_proof_url = dto.returnProofUrl
                    )
                }
            }
        } catch (e: Exception) {
            // SYNC BORROW offline
        }
    }
}