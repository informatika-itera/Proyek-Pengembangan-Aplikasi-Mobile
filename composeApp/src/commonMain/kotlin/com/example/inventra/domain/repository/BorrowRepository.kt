package com.example.inventra.domain.repository

import com.example.inventra.domain.model.BorrowRecord
import kotlinx.coroutines.flow.Flow

interface BorrowRepository {
    fun getAllRecords(): Flow<List<BorrowRecord>>
    fun getActiveRecords(): Flow<List<BorrowRecord>>
    suspend fun borrowItem(record: BorrowRecord): Long
    suspend fun returnItem(recordId: Long, proofImageUrl: String?)
    suspend fun approveRequest(recordId: Long)
    suspend fun approveReturn(recordId: Long)
    /** Paksa sync dari Supabase ke local cache */
    suspend fun refresh()
    /** Hapus semua data lokal dan di Supabase — hanya untuk demo reset */
    suspend fun deleteAll()
}