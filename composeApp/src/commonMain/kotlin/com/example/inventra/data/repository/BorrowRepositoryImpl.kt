package com.example.inventra.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.inventra.data.local.InventRaDatabase
import com.example.inventra.data.local.entity.toDomain
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.domain.repository.BorrowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class BorrowRepositoryImpl(
    database: InventRaDatabase
) : BorrowRepository {
    private val queries = database.borrowRecordQueries

    override fun getAllRecords(): Flow<List<BorrowRecord>> {
        return queries.getAllRecords()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getActiveRecords(): Flow<List<BorrowRecord>> {
        return queries.getActiveRecords()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun borrowItem(record: BorrowRecord): Long {
        return queries.transactionWithResult {
            queries.insertRecord(
                item_id = record.itemId,
                item_name = record.itemName,
                borrower_name = record.borrowerName,
                borrow_date = record.borrowDate.toEpochMilliseconds(),
                due_date = record.dueDate.toEpochMilliseconds(),
                return_date = record.returnDate?.toEpochMilliseconds(),
                status = record.status.name,
                fine_amount = record.fineAmount
            )
            queries.lastInsertId().executeAsOne()
        }
    }

    override suspend fun returnItem(recordId: Long) {
        val now = Clock.System.now()
        queries.updateRecordStatus(
            status = BorrowStatus.RETURNED.name,
            return_date = now.toEpochMilliseconds(),
            fine_amount = 0, // In a real app, calculate fine here
            id = recordId
        )
    }
}
