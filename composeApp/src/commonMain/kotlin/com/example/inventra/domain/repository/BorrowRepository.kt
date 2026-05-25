package com.example.inventra.domain.repository

import com.example.inventra.domain.model.BorrowRecord
import kotlinx.coroutines.flow.Flow

interface BorrowRepository {
    fun getAllRecords(): Flow<List<BorrowRecord>>
    fun getActiveRecords(): Flow<List<BorrowRecord>>
    suspend fun borrowItem(record: BorrowRecord): Long
    suspend fun returnItem(recordId: Long)
}
